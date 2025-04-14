// import Header from "../Header.vue";


const RETRIEVE_ALL = "/comercio";
const POST = "/comercio/create";
const DELETE = "/comercio/delete";
const RETRIEVE_ONE = "/comercio/login";
const UPDATE = "/comercio/update";
const LOGOUT = "/comercio/logout";

const RETRIEVE_ACTIVE = "/comercio/obtener";




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
            currentComercio: {},
            usuarioActivo: "sin sesion",
            showUserDropdown: false,  // Controla la visibilidad del dropdown de usuario
            showModal: false,         // Controla la visibilidad del modal
            modalMessage: '',         // Mensaje que se mostrará en el modal
            actionToConfirm: null,    // Acción que se va a confirmar (cerrar sesión/eliminar cuenta)
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
        // Mostrar/ocultar el dropdown del usuario
        toggleUserDropdown() {
            this.showUserDropdown = !this.showUserDropdown;
        },

        // Mostrar el modal para confirmar la acción de cerrar sesión
        confirmLogout() {
            this.modalMessage = '¿Estás seguro que quieres cerrar sesión?';
            this.actionToConfirm = this.logoutComercio;  // Establecer la acción que se confirmará
            this.showModal = true;  // Mostrar el modal
        },

        // Mostrar el modal para confirmar la eliminación de cuenta
        confirmDeleteAccount() {
            this.modalMessage = '¿Estás seguro que quieres eliminar tu cuenta?';
            this.actionToConfirm = this.deleteComercio;  // Establecer la acción que se confirmará
            this.showModal = true;  // Mostrar el modal
        },

        // Ejecutar la acción confirmada (cerrar sesión o eliminar cuenta)
        async confirmAction() {
            if (this.actionToConfirm) {
                await this.actionToConfirm();  // Ejecutar la acción confirmada
            }
            this.closeModal();  // Cerrar el modal después de la acción
        },

        // Cerrar el modal sin hacer nada
        closeModal() {
            this.showModal = false;
            this.actionToConfirm = null;
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

                        window.location.href = "./menu_productos.html";
                    }, 200); // 200ms suficiente

                    window.location.href = "./menu_productos.html"; // Página correcta
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

        async deleteComercio() {
            // Construimos la URL para el DELETE con el correo como parámetro
            const url = `${DELETE}`;

            try {
                // Realizamos la solicitud DELETE al backend
                const response = await axios.delete(url);

                // Verificamos si la eliminación fue exitosa
                if (response.status === 204) {
                    alert("Comercio eliminado con éxito");
                    window.location.href = "index.html"; // Redirigir a otra página si es necesario
                } else {
                    throw new Error("Error en la eliminación");
                }
            } catch (error) {
                console.error("Error al eliminar comercio:", error);
                alert("Hubo un problema con la eliminación del comercio.");
                window.location.href = "registroError.html"; // Redirigir en caso de error
            }
        }
        ,
        async logoutComercio() {
            try {
                // Realizamos la solicitud POST al backend para cerrar sesión
                const response = await axios.post(LOGOUT);

                // Verificamos si la respuesta fue exitosa
                if (response.status === 200) {
                    alert("Sesión cerrada con éxito");
                    window.location.href = "index.html"; // Redirigir al login o a la página deseada
                } else {
                    throw new Error("Error al cerrar sesión");
                }
            } catch (error) {
                console.error("Error al cerrar sesión:", error);
                alert("Hubo un problema al cerrar sesión.");
                window.location.href = "registroError.html"; // Redirigir en caso de error
            }
        },
        async getActiveComercio() {
            await axios.get(RETRIEVE_ACTIVE)
                .then((response) => {
                    this.usuarioActivo = response.data;
                    console.log(response.data);
                })
                .catch((error) => {
                    console.log("Error al obtener los datos:", error);
                });
        },

    },
    mounted() {
        // Solo ejecutar si estás en la página de modificar
        if (window.location.pathname.includes("modificar") || window.location.pathname.includes("comercioUPDATE") || window.location.pathname.includes("comercioREAD")) {
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
                        this.tipoComercio = comercio.negocio.tipo_negocio; // Tipo de comercio
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
        this.doGet();
        this.getActiveComercio()
    },
}).mount("#app"); //entiendo que hace que controlo lo que hay dentro del div de "app"