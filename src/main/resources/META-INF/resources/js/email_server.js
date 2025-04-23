const express = require("express");
const nodemailer = require("nodemailer");
const bodyParser = require("body-parser");
const cors = require("cors");
const app = express();

app.use(cors());
app.use(bodyParser.json({ limit: "10mb" }));

const transporter = nodemailer.createTransport({
  service: "gmail",
  auth: {
    user: "pediria.testing@gmail.com",
    pass: "knwp dbbz runw kabk"
  }
});

app.post("/enviar-informe", async (req, res) => {
  const { destinatario, pdfBase64 } = req.body;

  const mailOptions = {
    from: '"PedirIA" <pediria.testing@gmail.com>',
    to: destinatario,
    subject: "¡Informe de PedirIA!",
    text: "Adjuntamos el informe de stock en formato PDF.",
    attachments: [
      {
        filename: "informe_pediria.pdf",
        content: pdfBase64.split("base64,")[1],
        encoding: "base64"
      }
    ]
  };

  try {
    await transporter.sendMail(mailOptions);
    res.status(200).send("Correo enviado correctamente");
  } catch (error) {
    console.error("Error al enviar el correo:", error);
    res.status(500).send("Error al enviar el correo");
  }
});

const PORT = 3000;
app.listen(PORT, () => console.log(`Servidor iniciado en el puerto ${PORT}`));
