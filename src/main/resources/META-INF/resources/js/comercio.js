const RETRIEVE_ALL = "/comercio";
const POST = "/comercio/create";
const DELETE = "/comercio/delete/";
const RETRIEVE_ONE = "/comercio/retrieve/";
const UPDATE = "/comercio/update";

Vue.createApp({
    data() {
        return {
            comercios: [],
            correo: "",
            password: "",
            password2:"",
            nombre: "",
            tipoComercio: "",
            dia: "",
            currentComercio: {}
        };
    },
    methods: {
        async doGet() {
            await axios.get(RETRIEVE_ALL)
                .then((response) => {
                    this.comercios = response.data;
                    console.log(response.data);
                })
                .catch((error) => {
                    console.log("Error al obtener los datos:", error);
                });
        },

        async getComercio(correo) {
            console.log("Buscando comercio con correo:", correo);

            let url = RETRIEVE_ONE + correo;  // Construimos la URL del endpoint
            let self = this;  // Guardamos la referencia a `this` para usar dentro de `then`

            try {
                const response = await axios.get(url);

                if (response.status === 200) {
                    self.currentComercio = response.data;
                    console.log("Comercio encontrado:", response.data);
                } else {
                    console.warn("Comercio no encontrado. Estado HTTP:", response.status);
                }
            } catch (error) {
                if (error.response) {
                    console.error("Error al recuperar comercio:", error.response.data);
                    alert("No se encontró el comercio con el correo proporcionado.");
                } else if (error.request) {
                    console.error("No se recibió respuesta del servidor:", error.request);
                } else {
                    console.error("Error en la solicitud:", error.message);
                }
            }
        },

        async createComercio() {
            // Obtenemos los datos del formulario
            const correo = this.correo;
            const password = this.password;
            const tipoComercio = this.tipoComercio;
            const nombre = this.nombre;
            const diaCompraDeStock = this.dia;


            // Construimos la URL con los parámetros query
            const url = `${POST}?correo=${correo}&password=${password}&tipoComercio=${tipoComercio}&nombre=${nombre}&diaCompraDeStock=${diaCompraDeStock}`;

            try {
                // Realizamos el POST con los parámetros de la URL
                const response = await axios.post(url);

                if (response.status === 201) {
                    alert("Registro exitoso");
                    window.location.href = "registroCorrecto.html";
                } else {
                    throw new Error("Error en el registro");
                }
            } catch (error) {
                console.error("Error en el registro:", error);
                alert("Hubo un problema con el registro.");
                window.location.href = "registroError.html";
            }
        }
        ,


        async updateComercio() {
            console.log("Actualizando comercio...");
            let self = this;
            await axios.put(UPDATE, this.currentComercio)
                .then(function (response) {
                    console.log("Comercio actualizado:", response);
                    self.doGet();
                })
                .catch(function (error) {
                    console.log("Error al actualizar comercio:", error);
                });
        },

        async deleteComercio(correo) {

            //TODO: ESTA MAL
            console.log("Eliminando correo:", correo);
            let self = this;
            let url = DELETE + correo;
            await axios.delete(url)
                .then(function (response) {
                    console.log("Correo eliminado:", response);
                    self.doGet();
                })
                .catch(function (error) {
                    console.log("Error al eliminar correo:", error);
                });
        }
    },
    mounted() {
        this.doGet();
    }
}).mount("#app"); //entiendo que hace que controlo lo que hay dentro del div de "app"
