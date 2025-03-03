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

        async verificarComercio() {
            console.log("Verificando comercio con correo:", this.correo);

            let url = RETRIEVE_ONE + this.correo;

            try {
                const response = await axios.get(url);


                if (response.status === 200) {
                    let usuario = response.data;

                    if (usuario.password === this.password) {
                        alert("Inicio de sesión exitoso");
                        window.location.href = "registroCorrecto.html"; // Cambia por la página correcta
                    } else {
                        alert("Contraseña incorrecta");
                        window.location.href = "registroError.html"; // Cambia por la página de error
                    }
                } else {
                    alert("Correo no encontrado");
                    window.location.href = "registroError.html";
                }
            } catch (error) {
                console.error("Error al verificar el comercio:", error);
                alert("Hubo un problema con la verificación.");
                window.location.href = "registroError.html";
            }
        },


        async createComercio() {
            const comerciante = {
                correo: this.correo,
                password: this.password,
                nombre: this.nombre,
                tipoComercio: this.tipoComercio,
                dia: this.dia
            };

            try {
                const response = await axios.post(POST, comerciante);

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
        },


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
