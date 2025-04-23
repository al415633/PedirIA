const API_PESCADOS = "/pescados";
const GET_PESCADOS_PROPIOS = "/pescados/mis-productos";
const VALIDAR_PESCADO = "/pescados/validar";

const TIPOS_CONSERVA = ["REFRIGERADO", "FRESCO", "CONGELADO", "VIVO"];
const RETRIEVE_ACTIVE = "/comercio/obtener";
const LOGOUT = "/comercio/logout";
const DELETE = "/comercio/delete";

Vue.createApp({
    data() {
        return {
            busquedaNombre: '',

            // Gestión de los pescados
            pescados: [],
            nombrePescado: "",
            unidadPescado: "",
            tipoConserva: "",
            pescadoSeleccionado: {},
            imagenNombre: '',
            imagenTipo: '',
            imagenDatos: null,

            usuarioActivo: "sin sesion",
            showUserDropdown: false,  // Controla la visibilidad del dropdown de usuario
            showModal: false,         // Controla la visibilidad del modal
            modalMessage: '',         // Mensaje que se mostrará en el modal
            actionToConfirm: null,    // Acción que se va a confirmar (cerrar sesión/eliminar cuenta)


            // Paginación ¡
            currentPage: 1,
            itemsPerPage: 8,

            // Errores
            errores: { nombre: '', unidad: '', general: '' }
        };
    },
    // Permite hacer visible la enumeración constante    
    computed: {
        tiposConserva() {
            return TIPOS_CONSERVA;
        },
        displayedPescados() {
            const filtro = this.busquedaNombre.trim().toLowerCase();
            const pescadosFiltradas = this.pescados.filter(pescado =>
                pescado.nombre.toLowerCase().includes(filtro)
            );

            const start = (this.currentPage - 1) * this.itemsPerPage;
            return pescadosFiltradas.slice(start, start + this.itemsPerPage);
        },
        totalPages() {
            const filtro = this.busquedaNombre.trim().toLowerCase();
            const pescadosFiltradas = this.pescados.filter(pescado =>
                pescado.nombre.toLowerCase().includes(filtro)
            );
            return Math.ceil(pescadosFiltradas.length / this.itemsPerPage);
        },
        botonDeshabilitado() {
            return this.errores.nombre.length > 0 || this.errores.unidad.length > 0;
        }
    },
    methods: {
        // Validación de existencia del producto basándonos en el nombre y la unidad, al crear
        async validatePescado() {
            if (!this.nombrePescado || !this.unidadPescado) {
                this.errores.nombre = this.errores.unidad = '';
                return;
            }
            try {
                const { data } = await axios.get(`${VALIDAR_PESCADO}?nombre=${this.nombrePescado}&unidad=${this.unidadPescado}`);
                if (data.existe) {
                    this.errores.nombre = "Ya existe un pescado con este nombre.";
                    this.errores.unidad = "Por favor, elija otro nombre o unidad.";
                } else {
                    this.errores.nombre = this.errores.unidad = '';
                }
            } catch (error) {
                console.error("Error en la validación:", error);
                this.errores.nombre = "Error al validar.";
            }
        },

        // Manejo de archivo de imagen
        onFileChange(event) {
            const file = event.target.files[0];
            if (!file) return;

            this.imagenNombre = file.name;
            this.imagenTipo = file.type;

            const reader = new FileReader();
            reader.onload = (e) => {
                this.imagenDatos = e.target.result.split(',')[1]; // Separa el encabezado de Base64
            };
            reader.readAsDataURL(file);
        },

        // Métodos para paginación
        prevPage() {
            if (this.currentPage > 1) this.currentPage--;
        },
        nextPage() {
            if (this.currentPage < this.totalPages) this.currentPage++;
        },
        goToPage(page) {
            this.currentPage = page;
        },

        async getAllPescados() {
            try {
                const { data } = await axios.get(`${GET_PESCADOS_PROPIOS}`);
                this.pescados = data.map(pescadoArray => {
                    const pescado = pescadoArray[0]; // Extrae el objeto pescado del array interno
                    return {
                        ...pescado,
                        imagenNombre: pescadoArray[1] || "",
                        imagenTipo: pescadoArray[2] || "",
                        imagenDatos: pescadoArray[3] || null
                    };
                });
                console.log(this.pescados)
                this.pescados.sort((a, b) => a.nombre.localeCompare(b.nombre));
            } catch (error) {
                console.error("Error al obtener pescados:", error);
            }
        },

        // Recuperar una pescado específico
        async retrievePescado(id) {
            try {
                const { data } = await axios.get(`${API_PESCADOS}/${id}`);
                this.pescadoSeleccionado = data;
            } catch (error) {
                console.error("Error al recuperar el pescado:", error);
            }
        },

        // Crear un nuevo pescado
        async createPescado() {
            if (this.errores.nombre || this.errores.unidad) return; // Evita si hay errores

            try {
                const newPescado = {
                    nombre: this.nombrePescado,
                    unidad: this.unidadPescado,
                    tipoConserva: this.tipoConserva,
                    imagenNombre: this.imagenNombre,
                    imagenTipo: this.imagenTipo,
                    imagenDatos: this.imagenDatos
                };

                const { data } = await axios.post(API_PESCADOS, newPescado);
                this.pescados.push(data);
                this.pescados.sort((a, b) => a.nombre.localeCompare(b.nombre));

                // Limpiar formulario
                this.resetForm();
                this.showToast('¡Pescado añadido con éxito!', 'bg-success');
            } catch (error) {
                console.error("Error al crear Pescado:", error);
                this.showToast('Error al crear el pescado.', 'bg-danger');
            }
        },


        // Reinicia los campos del formulario
        resetForm() {
            this.nombrePescado = "";
            this.unidadPescado = "";
            this.tipoConserva = "";
            this.imagenNombre = "";
            this.imagenTipo = "";
            this.imagenDatos = null;
            this.errores = { nombre: '', unidad: '', general: '' };
        },

        // Mostrar notificaciones tipo Toast
        showToast(message, colorClass) {
            const toastBody = document.getElementById('toast-body');
            toastBody.textContent = message;
            const toastEl = document.getElementById('toastPescado');
            toastEl.classList.add(colorClass);
            const toast = new bootstrap.Toast(toastEl);
            toast.show();
        },

        toggleUserDropdown() {
            this.showUserDropdown = !this.showUserDropdown;
        },

        // Mostrar el modal para confirmar la acción de cerrar sesión
        confirmLogout() {
            //console.log("confirm 1");
            //this.modalMessage = '¿Estás seguro que quieres cerrar sesión?';
            this.actionToConfirm = this.logoutComercio();  // Establecer la acción que se confirmará
            //this.showModal = true;  // Mostrar el modal

        },
        async deleteComercio() {
            console.log("llego al delete")
            // Construimos la URL para el DELETE con el correo como parámetro
            const url = `${DELETE}`;

            const confirmacion = window.confirm('¿Estás seguro que quieres eliminar tu cuenta?');
            if (confirmacion) {
                try {
                    // Realizamos la solicitud DELETE al backend
                    const response = await axios.delete(url);

                    // Verificamos si la eliminación fue exitosa
                    if (response.status === 204) {
                        alert("Comercio eliminado con éxito");
                        window.location.href = "../index.html"; // Redirigir a otra página si es necesario
                    } else {
                        throw new Error("Error en la eliminación");
                    }

                } catch (error) {
                    console.error("Error al eliminar comercio:", error);
                    alert("Hubo un problema con la eliminación del comercio.");
                    window.location.href = "registroError.html"; // Redirigir en caso de error
                }
            }}

        ,
        async logoutComercio() {
            try {
                console.log("logout 1");

                const confirmacion = window.confirm('¿Estás seguro que quieres cerrar sesión?');
                if (confirmacion) {
                    // Realizamos la solicitud POST al backend para cerrar sesión
                    const response = await axios.post(LOGOUT);

                    // Verificamos si la respuesta fue exitosa
                    if (response.status === 200) {
                        alert("Sesión cerrada con éxito");
                        window.location.href = "../index.html"; // Redirigir al login o a la página deseada
                    } else {
                        throw new Error("Error al cerrar sesión");
                    }
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

        // Mostrar el modal para confirmar la eliminación de cuenta
        confirmDeleteAccount() {
            this.modalMessage = '¿Estás seguro que quieres eliminar tu cuenta?';
            this.actionToConfirm = this.deleteComercio();  // Establecer la acción que se confirmará
            //this.showModal = true;  // Mostrar el modal
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
    },
    mounted() {
        this.getAllPescados();
        this.getActiveComercio()
    }
}).mount("#app");
