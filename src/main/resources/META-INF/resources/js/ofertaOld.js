const RETRIEVE_ALL = "/oferta";
const POST = "/oferta/create";
const DELETE = "/oferta/delete/";
const RETRIEVE_ONE = "/oferta/retrieve";
const UPDATE = "/oferta/update";

Vue.createApp({
    data() {
        return {
            ofertas: [],
            lugar: "",
            idNegocio: "",
            idAprovechante: "",
            fechaAlta: "",
            fechaBaja: "",
            fechaVencimiento: "",
            cantidad: "",
            idProducto: "",
            tipoProducto: "",
            currentOferta: {}
        };
    },
    methods: {
        async doGet() {
            await axios.get(RETRIEVE_ALL)
                .then((response) => {
                    this.ofertas = response.data;
                    console.log(response.data);
                })
                .catch((error) => {
                    console.log("Error al obtener los datos:", error);
                });
        },

        async verificarOferta() {
            const idProducto = this.idProducto;

            console.log("Verificando oferta con idProducto:", idProducto);

            // Construimos la URL con los parámetros en la query
            const url = `${RETRIEVE_ONE}?idProducto=${encodeURIComponent(idProducto)}`;

            try {
                // Enviar la petición POST sin cuerpo, solo con los parámetros en la URL
                const response = await axios.post(url);

                if (response.status === 200) {
                    alert("Oferta verificada exitosamente");

                    // PEQUEÑO RETRASO para asegurar que la cookie se guarda
                    setTimeout(() => {
                        window.location.href = "./ofertaUPDATE.html";
                    }, 200); // 200ms suficiente

                    window.location.href = "./ofertaUPDATE.html"; // Página correcta
                } else {
                    throw new Error("Error en la verificación");
                }
            } catch (error) {
                console.error("Error al verificar la oferta:", error);
                alert("Error al verificar la oferta.");
                window.location.href = "registroError.html";
            }
        },

        leerCookie(nombre) {
            const cookies = document.cookie.split("; ");
            for (let i = 0; i < cookies.length; i++) {
                const [key, value] = cookies[i].split("=");
                if (key === nombre) {
                    return decodeURIComponent(value);
                }
            }
            return null;
        },

        async createOferta() {
            // Obtenemos los datos del formulario
            const lugar = this.lugar;
            const idNegocio = this.idNegocio;
            const idAprovechante = this.idAprovechante;
            const fechaAlta = this.fechaAlta;
            const fechaBaja = this.fechaBaja;
            const fechaVencimiento = this.fechaVencimiento;
            const cantidad = this.cantidad;
            const idProducto = this.idProducto;
            const tipoProducto = this.tipoProducto;

            // Construimos la URL con los parámetros query
            const url = `${POST}?lugar=${lugar}&idNegocio=${idNegocio}&idAprovechante=${idAprovechante}&fechaAlta=${fechaAlta}&fechaBaja=${fechaBaja}&fechaVencimiento=${fechaVencimiento}&cantidad=${cantidad}&idProducto=${idProducto}&tipoProducto=${tipoProducto}`;

            try {
                // Realizamos el POST con los parámetros de la URL
                const response = await axios.post(url);

                if (response.status === 201) {
                    alert("Oferta registrada exitosamente");
                    window.location.href = "menu_ofertas.html";
                } else {
                    throw new Error("Error en el registro");
                }
            } catch (error) {
                console.error("Error en el registro de la oferta:", error);
                alert("Hubo un problema con el registro de la oferta.");
                window.location.href = "registroError.html";
            }
        },

        async updateOferta() {
            // Obtenemos los datos del formulario
            const lugar = this.lugar;
            const idNegocio = this.idNegocio;
            const idAprovechante = this.idAprovechante;
            const fechaAlta = this.fechaAlta;
            const fechaBaja = this.fechaBaja;
            const fechaVencimiento = this.fechaVencimiento;
            const cantidad = this.cantidad;
            const idProducto = this.idProducto;
            const tipoProducto = this.tipoProducto;

            const url = `${UPDATE}?lugar=${lugar}&idNegocio=${idNegocio}&idAprovechante=${idAprovechante}&fechaAlta=${fechaAlta}&fechaBaja=${fechaBaja}&fechaVencimiento=${fechaVencimiento}&cantidad=${cantidad}&idProducto=${idProducto}&tipoProducto=${tipoProducto}`;

            // Realizamos la solicitud PUT al backend para actualizar la oferta
            try {
                const response = await axios.put(url);
                if (response.status === 204) {
                    alert("Oferta actualizada con éxito");
                    window.location.href = "menu_ofertas.html"; // Redirigir a otra página si es necesario
                } else {
                    throw new Error("Error en la actualización");
                }
            } catch (error) {
                console.error("Error al actualizar oferta:", error);
                alert("Hubo un problema con la actualización de la oferta.");
                window.location.href = "registroError.html"; // Redirigir en caso de error
            }
        },

        async deleteOferta(idOferta) {
            console.log("Eliminando oferta con ID:", idOferta);
            let self = this;
            let url = DELETE + idOferta;
            await axios.delete(url)
                .then(function (response) {
                    console.log("Oferta eliminada:", response);
                    self.doGet();
                })
                .catch(function (error) {
                    console.log("Error al eliminar la oferta:", error);
                });
        }
    },
    mounted() {
        // Solo ejecutar si estás en la página de modificar
        if (window.location.pathname.includes("modificar") || window.location.pathname.includes("ofertaUPDATE")) {
            const idProducto = this.leerCookie("producto");

            console.log("ID de producto leído de cookie (crudo):", idProducto);

            if (idProducto) {
                // Limpiamos posibles comillas
                const idProductoLimpio = idProducto.replace(/(^")|("$)/g, "");
                console.log("ID de producto limpio:", idProductoLimpio);

                axios.get(`/oferta/retrieve/${idProductoLimpio}`)
                    .then(response => {
                        console.log("Respuesta del backend:", response.data);
                        const oferta = response.data;

                        // Asignamos campos
                        this.lugar = oferta.lugar;
                        this.idNegocio = oferta.id_negocio;
                        this.idAprovechante = oferta.id_aprovechante;
                        this.fechaAlta = oferta.fecha_alta;
                        this.fechaBaja = oferta.fecha_baja;
                        this.fechaVencimiento = oferta.fecha_vencimiento;
                        this.cantidad = oferta.cantidad;
                        this.idProducto = oferta.id_producto;
                        this.tipoProducto = oferta.tipo_producto;
                    })
                    .catch(error => {
                        console.error("Error al obtener datos de la oferta:", error);
                    });
            } else {
                console.warn("No se encontró la cookie");
            }
        }
    }

}).mount("#app"); // Esto hace que controle lo que hay dentro del div de "app"
