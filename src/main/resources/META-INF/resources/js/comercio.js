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

                    // PEQUEÑO RETRASO para asegurar que la cookie se guarda
                    setTimeout(() => {
                        window.location.href = "./comercioUPDATE.html";
                    }, 200); // 200ms suficiente

                    window.location.href = "./comercioUPDATE.html"; // Página correcta
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
            // Obtenemos los datos del formulario
            const correo = this.correo;
            const password = this.password;
            const nombre = this.nombre;
            const diaCompraDeStock = this.dia;


            const url = `${UPDATE}?correo=${correo}&password=${password}&nombre=${nombre}&diaCompraDeStock=${diaCompraDeStock}`;

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
        }
        ,

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
        // Solo ejecutar si estás en la página de modificar
        if (window.location.pathname.includes("modificar") || window.location.pathname.includes("comercioUPDATE")) {
            const correo = this.leerCookie("usuario");

            console.log("Correo leído de cookie (crudo):", correo);

            if (correo) {
                // Limpiamos posibles comillas
                const correoLimpio = correo.replace(/(^")|("$)/g, "");
                console.log("Correo limpio:", correoLimpio);

                axios.get(`/comercio/retrieve/${correoLimpio}`)
                    .then(response => {
                        console.log("Respuesta del backend:", response.data);
                        const comercio = response.data;

                        // Asignamos campos
                        this.correo = comercio.correo;
                        this.nombre = comercio.negocio.nombre; // Nombre del comercio
                        this.tipoComercio = comercio.negocio.tipo; // Tipo de comercio
                        this.dia = comercio.negocio.diaCompraDeStock; // Día de compra
                        this.password = comercio.password;

                    })
                    .catch(error => {
                        console.error("Error al obtener datos del comercio:", error);
                    });
            } else {
                console.warn("No se encontró la cookie");
            }
        }
    }




}).mount("#app"); //entiendo que hace que controle lo que hay dentro del div de "app"
