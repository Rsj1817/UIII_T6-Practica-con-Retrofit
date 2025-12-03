# app.py
from flask import Flask, request, jsonify, send_from_directory, abort
from flask_sqlalchemy import SQLAlchemy
from werkzeug.utils import secure_filename
import os
import threading
import socket

# Config
BASE_DIR = os.path.dirname(os.path.abspath(__file__))
UPLOAD_FOLDER = os.path.join(BASE_DIR, "uploads")
os.makedirs(UPLOAD_FOLDER, exist_ok=True)

app = Flask(__name__)
app.config["SQLALCHEMY_DATABASE_URI"] = "sqlite:///" + os.path.join(BASE_DIR, "items.db")
app.config["SQLALCHEMY_TRACK_MODIFICATIONS"] = False
app.config["UPLOAD_FOLDER"] = UPLOAD_FOLDER
app.config["MAX_CONTENT_LENGTH"] = 16 * 1024 * 1024  # 16 MB max upload

db = SQLAlchemy(app)

# Modelo SQLAlchemy equivalente a tu data class Item
class Item(db.Model):
    __tablename__ = "items"
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(200), nullable=False)
    description = db.Column(db.Text, nullable=True)
    category = db.Column(db.String(120), nullable=True)
    image_url = db.Column(db.String(400), nullable=True)

    def to_dict(self):
        return {
            "id": self.id,
            "name": self.name,
            "description": self.description,
            "category": self.category,
            "imageUrl": self.image_url
        }

def items_table_html():
    items = Item.query.order_by(Item.id.asc()).all()
    rows = "\n".join([
        f"<tr><td>{i.id}</td><td>{i.name or ''}</td><td>{i.description or ''}</td><td>{i.category or ''}</td><td>{i.image_url or ''}</td></tr>"
        for i in items
    ])
    return (
        "<!doctype html><html><head><meta charset=\"utf-8\"><title>Items</title>"
        "<style>table{border-collapse:collapse;width:100%}th,td{border:1px solid #ddd;padding:8px}th{background:#f4f4f4;text-align:left}</style>"
        "</head><body><h1>Items</h1><table><thead><tr><th>ID</th><th>Nombre</th><th>Descripción</th><th>Categoría</th><th>Imagen</th></tr></thead><tbody>"
        + rows + "</tbody></table></body></html>"
    )

def print_items_table():
    items = Item.query.order_by(Item.id.asc()).all()
    def fmt(s, w):
        s = s or ""
        return s[:w].ljust(w)
    print("\nID  | Nombre              | Descripción                        | Categoría       | Imagen")
    print("-" * 100)

def write_items_txt():
    items = Item.query.order_by(Item.id.asc()).all()
    lines = [
        f"ID: {i.id} | Nombre: {i.name or ''} | Descripción: {i.description or ''} | Categoría: {i.category or ''} | Imagen: {i.image_url or ''}"
        for i in items
    ]
    path = os.path.join(BASE_DIR, "items.txt")
    with open(path, "w", encoding="utf-8") as f:
        f.write("\n".join(lines) + ("\n" if lines else ""))

def _get_local_ip():
    s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    try:
        s.connect(("8.8.8.8", 80))
        ip = s.getsockname()[0]
    except Exception:
        ip = "127.0.0.1"
    finally:
        s.close()
    return ip

def _udp_discovery_server():
    sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    try:
        sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        sock.bind(("0.0.0.0", 5001))
        while True:
            data, addr = sock.recvfrom(1024)
            try:
                msg = data.decode("utf-8", errors="ignore").strip()
            except Exception:
                msg = ""
            if msg == "DISCOVER_RETROFIT_API":
                base_url = f"http://{_get_local_ip()}:5000/"
                resp = f"RETROFIT_API {base_url}"
                sock.sendto(resp.encode("utf-8"), addr)
    except Exception:
        pass

# --- Endpoints ---

# GET todos los items
@app.route("/items", methods=["GET"])
def get_items():
    items = Item.query.order_by(Item.id.asc()).all()
    write_items_txt()
    return jsonify([i.to_dict() for i in items]), 200

# GET item por id
@app.route("/items/<int:item_id>", methods=["GET"])
def get_item(item_id):
    item = Item.query.get(item_id)
    if not item:
        return jsonify({"error": "Item no encontrado"}), 404
    return jsonify(item.to_dict()), 200

# POST multipart -> crea nuevo item (campos: name, description, category) + optional file 'image'
@app.route("/items", methods=["POST"])
def create_item():
    name = request.form.get("name")
    if not name:
        return jsonify({"error": "El campo 'name' es requerido"}), 400
    description = request.form.get("description", "")
    category = request.form.get("category", "")

    file = request.files.get("image")
    image_url = None
    if file:
        filename = secure_filename(file.filename)
        if filename == "":
            return jsonify({"error": "Nombre de archivo inválido"}), 400
        save_path = os.path.join(app.config["UPLOAD_FOLDER"], filename)
        base, ext = os.path.splitext(filename)
        counter = 1
        while os.path.exists(save_path):
            filename = f"{base}_{counter}{ext}"
            save_path = os.path.join(app.config["UPLOAD_FOLDER"], filename)
            counter += 1
        file.save(save_path)
        image_url = f"/uploads/{filename}"

    item = Item(name=name, description=description, category=category, image_url=image_url)
    db.session.add(item)
    db.session.commit()
    print_items_table()
    write_items_txt()
    return jsonify(item.to_dict()), 201

# PUT multipart -> actualiza item (puede actualizar campos y reemplazar imagen)
@app.route("/items/<int:item_id>", methods=["PUT"])
def update_item(item_id):
    item = Item.query.get(item_id)
    if not item:
        return jsonify({"error": "Item no encontrado"}), 404

    name = request.form.get("name")
    description = request.form.get("description")
    category = request.form.get("category")
    file = request.files.get("image")

    if name is not None and name != "":
        item.name = name
    if description is not None:
        item.description = description
    if category is not None:
        item.category = category

    if file:
        filename = secure_filename(file.filename)
        if filename == "":
            return jsonify({"error": "Nombre de archivo inválido"}), 400
        save_path = os.path.join(app.config["UPLOAD_FOLDER"], filename)
        base, ext = os.path.splitext(filename)
        counter = 1
        while os.path.exists(save_path):
            filename = f"{base}_{counter}{ext}"
            save_path = os.path.join(app.config["UPLOAD_FOLDER"], filename)
            counter += 1
        file.save(save_path)
        # eliminar el archivo anterior si existe en uploads
        if item.image_url:
            try:
                old_filename = item.image_url.replace("/uploads/", "")
                old_path = os.path.join(app.config["UPLOAD_FOLDER"], old_filename)
                if os.path.exists(old_path):
                    os.remove(old_path)
            except Exception:
                pass
        item.image_url = f"/uploads/{filename}"

    db.session.commit()
    print_items_table()
    write_items_txt()
    return jsonify(item.to_dict()), 200

# DELETE item
@app.route("/items/<int:item_id>", methods=["DELETE"])
def delete_item(item_id):
    item = Item.query.get(item_id)
    if not item:
        return jsonify({"error": "Item no encontrado"}), 404
    if item.image_url:
        try:
            old_filename = item.image_url.replace("/uploads/", "")
            old_path = os.path.join(app.config["UPLOAD_FOLDER"], old_filename)
            if os.path.exists(old_path):
                os.remove(old_path)
        except Exception:
            pass
    db.session.delete(item)
    db.session.commit()
    print_items_table()
    write_items_txt()
    return jsonify({"message": "Item eliminado"}), 200

# Servir archivos subidos (para desarrollo)
@app.route("/uploads/<path:filename>", methods=["GET"])
def uploaded_file(filename):
    return send_from_directory(app.config["UPLOAD_FOLDER"], filename)

# Root
@app.route("/", methods=["GET"])
def index():
    return jsonify({"message": "API de Items - Flask + SQLite", "endpoints": ["/items", "/items/<id>"]})


@app.route("/items-table", methods=["GET"])
def items_table():
    return items_table_html()

if __name__ == "__main__":
    # Crear tablas de DB de forma segura (sin depender de decoradores)
    with app.app_context():
        db.create_all()

    # Iniciar servidor UDP de descubrimiento en hilo aparte
    threading.Thread(target=_udp_discovery_server, daemon=True).start()

    # Ejecutar en modo dev
    app.run(host="0.0.0.0", port=5000, debug=True)
