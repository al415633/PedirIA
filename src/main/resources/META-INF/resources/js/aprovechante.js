const RETRIEVE_ALL = "/aprovechante";
const POST = "/aprovechante/create";
const DELETE = "/aprovechante/delete";
const RETRIEVE_ONE = "/aprovechante/login";
const UPDATE = "/aprovechante/update";
const LOGOUT = "/aprovechante/logout";

const RETRIEVE_ACTIVE = "/aprovechante/obtener";


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
            currentAprovechante: {},
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
                    this.aprovechantes = response.data;
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
            this.actionToConfirm = this.logoutAprovechante;  // Establecer la acción que se confirmará
            this.showModal = true;  // Mostrar el modal
        },

        // Mostrar el modal para confirmar la eliminación de cuenta
        confirmDeleteAccount() {
            this.modalMessage = '¿Estás seguro que quieres eliminar tu cuenta?';
            this.actionToConfirm = this.deletAprovechante;  // Establecer la acción que se confirmará
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

                    // PEQUEÑO RETRASO para asegurar que la cookie se guarda
                    setTimeout(() => {

                        window.location.href = "./oferta.html";
                    }, 200); // 200ms suficiente

                    window.location.href = "./oferta.html"; // Página correcta
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
                    window.location.href = "oferta.html";
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

            // Realizamos la solicitud PUT al backend para actualizar el aprovechante
            try {
                //const response = await axios.put(UPDATE, aprovechante);
                const response = await axios.put(url);
                // Verificamos si la actualización fue exitosa
                if (response.status === 204) {
                    alert("Aprovechante actualizado con éxito");
                    window.location.href = "oferta.html"; // Redirigir a otra página si es necesario
                } else {
                    throw new Error("Error en la actualización");
                }
            } catch (error) {
                console.error("Error al actualizar aprovechante:", error);
                alert("Hubo un problema con la actualización del aprovechante.");
                window.location.href = "registroError.html"; // Redirigir en caso de error
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
        }
        ,

        async deletAprovechante() {
            console.log("llego al delete")
            // Construimos la URL para el DELETE con el correo como parámetro
            const url = `${DELETE}`;

            try {
                // Realizamos la solicitud DELETE al backend
                const response = await axios.delete(url);

                // Verificamos si la eliminación fue exitosa
                if (response.status === 204) {
                    alert("Aprovechante eliminado con éxito");
                    window.location.href = "index.html"; // Redirigir a otra página si es necesario
                } else {
                    throw new Error("Error en la eliminación");
                }
            } catch (error) {
                console.error("Error al eliminar aprovechante:", error);
                alert("Hubo un problema con la eliminación del aprovechante.");
                window.location.href = "registroError.html"; // Redirigir en caso de error
            }
        }
        ,
        async logoutAprovechante() {
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
        async getActiveAprovechante() {
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
        if (window.location.pathname.includes("modificar") || window.location.pathname.includes("aprovechanteUPDATE") || window.location.pathname.includes("aprovechanteREAD")) {
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
                        this.tipoAprovechante = aprovechante.aprovechante.tipo_aprovechante; // Tipo de aprovechante
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
        this.doGet();
        this.getActiveAprovechante();
        console.log("Pagina cargada, showModal inicial:", this.showModal);
        this.showModal = false;

    }
}).mount("#app"); //entiendo que hace que controlo lo que hay dentro del div de "app"
