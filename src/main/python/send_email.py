from flask import Flask, request, jsonify
from flask_cors import CORS
from email.message import EmailMessage
import smtplib
import base64

app = Flask(__name__)
CORS(app)  # Permitir peticiones desde el frontend

EMAIL_USER = "pediria.testing@gmail.com"
EMAIL_PASS = "123PEDIRIA321"
SMTP_SERVER = "smtp.gmail.com"
SMTP_PORT = 465


@app.route("/enviar-pdf", methods=["POST"])
def enviar_pdf():
    print("GOLLLLLLLLLLLLLLLLLLLLLLLLLL")
    data = request.json
    archivo_base64 = data["archivo"]
    nombre = data["nombreArchivo"]
    correo = data["correo"]

    # Crear mensaje
    msg = EmailMessage()
    msg["Subject"] = "Informe de Stock de Carne"
    msg["From"] = EMAIL_USER
    msg["To"] = correo
    msg.set_content("Adjunto se encuentra el informe solicitado.")

    # Decodificar PDF
    try:
        pdf_data = base64.b64decode(archivo_base64)
        msg.add_attachment(
            pdf_data, maintype="application", subtype="pdf", filename=nombre
        )
    except Exception as e:
        return jsonify({"exito": False, "mensaje": f"Error al procesar el PDF: {e}"})

    # Enviar correo
    try:
        with smtplib.SMTP_SSL(SMTP_SERVER, SMTP_PORT) as smtp:
            smtp.login(EMAIL_USER, EMAIL_PASS)
            smtp.send_message(msg)
        return jsonify({"exito": True})
    except Exception as e:
        return jsonify({"exito": False, "mensaje": f"Error al enviar el correo: {e}"})


if __name__ == "__main__":
    app.run(port=5000)
