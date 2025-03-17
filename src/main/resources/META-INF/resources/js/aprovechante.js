const RETRIEVE_ALL = "/aprovechante";
const POST = "/aprovechante/create";
const DELETE = "/aprovechante/delete/";
const RETRIEVE_ONE = "/aprovechante/login";
const UPDATE = "/aprovechante/update";



Vue.createApp({
    data() {
        return {
            aprovechantes: [],
            correo: "",
            password: "",
            password2:"",
            tipoAprovechante: "",
            condiciones: "",
            condiciones2: "",
            currentAprovechante: {}
        };
    },
    methods: {
        async doGet() {
            await axios.get(RETRIEVE_ALL)
                .then((response) => {
                    this.aprovechantes = response.data;
                    console.log(response.data);
                })
                .catch((error) => {
                    console.log("Error al obtener los datos:", error);
                });
        },

        async verificarAprovechante() {
            const correo = this.correo;
            const password = this.password;

            console.log("Verificando aprovechante con correo:", correo);

            // Construimos la URL con los parámetros en la query
            const url = `${RETRIEVE_ONE}?correo=${encodeURIComponent(correo)}&password=${encodeURIComponent(password)}`;

            try {
                // Enviar la petición POST sin cuerpo, solo con los parámetros en la URL
                const response = await axios.post(url);

                if (response.status === 200) {
                    alert("Inicio de sesión exitoso");
                    window.location.href = "registroCorrecto.html"; // Página correcta
                } else {
                    throw new Error("Error en la autenticación");
                }
            } catch (error) {
                console.error("Error al verificar el aprovechante:", error);
                alert("Correo o contraseña incorrectos.");
                window.location.href = "registroError.html";
            }
        }

        ,


        async createAprovechante() {
            // Obtenemos los datos del formulario
            const correo = this.correo;
            const password = this.password;
            const tipoAprovechante = this.tipoAprovechante;
            const condiciones = this.condiciones;
            const condiciones2 = this.condiciones2;



            // Construimos la URL con los parámetros query
            const url = `${POST}?correo=${correo}&password=${password}&tipoAprovechante=${tipoAprovechante}&condiciones=${condiciones}&condiciones2=${condiciones2}`;

            console.log(url);
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


        async updateAprovechante() {
            console.log("Actualizando aprovechante...");
            let self = this;
            await axios.put(UPDATE, this.currentAprovechante)
                .then(function (response) {
                    console.log("Aprovechante actualizado:", response);
                    self.doGet();
                })
                .catch(function (error) {
                    console.log("Error al actualizar aprovechannte:", error);
                });
        },

        async deleteAprovechante(correo){

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
