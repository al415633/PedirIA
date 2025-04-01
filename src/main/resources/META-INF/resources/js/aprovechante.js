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
                    window.location.href = "menu_productos.html"; // Página correcta
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
            // Obtenemos los datos del formulario
            const correo = this.correo;
            const password = this.password;
            const condiciones = this.condiciones;
            const condiciones2 = this.condiciones2;


            const url = `${UPDATE}?correo=${correo}&password=${password}&condiciones=${condiciones}&condiciones2=${condiciones2}`;

            // Realizamos la solicitud PUT al backend para actualizar el comercio
            try {
                //const response = await axios.put(UPDATE, comercio);
                const response = await axios.put(url);
                // Verificamos si la actualización fue exitosa
                if (response.status === 204) {
                    alert("Comercio actualizado con éxito");
                    window.location.href = "menu_productos.html"; // Redirigir a otra página si es necesario
                } else {
                    throw new Error("Error en la actualización");
                }
            } catch (error) {
                console.error("Error al actualizar comercio:", error);
                alert("Hubo un problema con la actualización del comercio.");
                window.location.href = "registroError.html"; // Redirigir en caso de error
            }
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
        }
        ,
    },
    mounted() {
        // Solo ejecutar si estás en la página de modificar
        if (window.location.pathname.includes("modificar") || window.location.pathname.includes("aprovechanteUPDATE")) {
            const correo = this.leerCookie("usuario");

            console.log("Correo leído de cookie (crudo):", correo);

            if (correo) {
                // Limpiamos posibles comillas
                const correoLimpio = correo.replace(/(^")|("$)/g, "");
                console.log("Correo limpio:", correoLimpio);

                axios.get(`/aprovechante/retrieve/${correoLimpio}`)
                    .then(response => {
                        console.log("Respuesta del backend:", response.data);

                        const aprovechante = response.data;
                        console.log("Respuesta del backend aprovechante:", aprovechante.aprovechante);


                        // Asignamos campos
                        this.correo = aprovechante.correo;
                        this.tipoAprovechante = aprovechante.aprovechante.tipo_aprovechante; // Tipo de comercio
                        this.condiciones = aprovechante.aprovechante.condiciones; // condiciones
                        this.condiciones2 = aprovechante.aprovechante.condiciones2; // condiciones2
                        this.password = aprovechante.password;

                    })
                    .catch(error => {
                        console.error("Error al obtener datos del aprovechante:", error);
                    });
            } else {
                console.warn("No se encontró la cookie");
            }
        }
    }
}).mount("#app"); //entiendo que hace que controlo lo que hay dentro del div de "app"
