const RETRIEVE_ALL = "/comercio";
const POST = "/comercio/create";
const DELETE = "/comercio/delete/";
const RETRIEVE_ONE = "/comercio/retrieve/";
const UPDATE = "/comercio/update";

Vue.createApp({
    data() {
        return {
            comerciantes: [],
            correo: "",
            password: "",
            nombre: "",
            tipoComercio: "",
            dia: "",
            currentComerciante: {}
        };
    },
    methods: {
        async doGet() {
            await axios.get(RETRIEVE_ALL)
                .then((response) => {
                    this.comerciantes = response.data;
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
                    self.currentComerciante = response.data;
                    console.log("Comerciante encontrado:", response.data);
                } else {
                    console.warn("Comerciante no encontrado. Estado HTTP:", response.status);
                }
            } catch (error) {
                if (error.response) {
                    console.error("Error al recuperar comerciante:", error.response.data);
                    alert("No se encontró el comerciante con el correo proporcionado.");
                } else if (error.request) {
                    console.error("No se recibió respuesta del servidor:", error.request);
                } else {
                    console.error("Error en la solicitud:", error.message);
                }
            }
        },

        async createComercio(correo, password, nombre, tipoComercio, dia) {
            let self = this;
            let comerciante = {
                correo: correo,
                password: password,
                nombre: nombre,
                tipoComercio: tipoComercio,
                dia: dia
            };

            await axios.post(POST, comerciante)
                .then(function (response) {
                    console.log("Comerciante creado:", response);
                    self.doGet();
                })
                .catch(function (error) {
                    console.log("Error al crear comerciante:", error);
                });
        },

        async updateComercio() {
            console.log("Actualizando comerciante...");
            let self = this;
            await axios.put(UPDATE, this.currentComerciante)
                .then(function (response) {
                    console.log("Comerciante actualizado:", response);
                    self.doGet();
                })
                .catch(function (error) {
                    console.log("Error al actualizar comerciante:", error);
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
}).mount("#app");
