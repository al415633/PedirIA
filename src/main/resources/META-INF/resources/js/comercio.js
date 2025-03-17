const RETRIEVE_ALL = "/comercio";
const POST = "/comercio/create";
const DELETE = "/comercio/delete/";
const RETRIEVE_ONE = "/comercio/login";
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
            const correo = this.correo;
            const password = this.password;

            console.log("Verificando comercio con correo:", correo);

            // Construimos la URL con los parámetros en la query
            const url = `${RETRIEVE_ONE}?correo=${encodeURIComponent(correo)}&password=${encodeURIComponent(password)}`;

            try {
                // Enviar la petición POST sin cuerpo, solo con los parámetros en la URL
                const response = await axios.post(url);

                if (response.status === 200) {
                    alert("Inicio de sesión exitoso");
                    window.location.href = "menu_productos.html"; // Página correcta
                } else {
                    throw new Error("Error en la autenticación");
                }
            } catch (error) {
                console.error("Error al verificar el comercio:", error);
                alert("Correo o contraseña incorrectos.");
                window.location.href = "registroError.html";
            }
        }

        ,


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
                    window.location.href = "menu_productos.html";
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
