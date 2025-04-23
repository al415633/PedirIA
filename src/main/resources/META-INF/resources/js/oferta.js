const { createApp } = Vue;
const API_OFERTAS = "/oferta";

const RETRIEVE_ACTIVE = "/aprovechante/obtener";
const LOGOUT = "/aprovechante/logout";
const DELETE = "/aprovechante/delete";


createApp({
    data() {
        return {
            ofertas: [],
            ofertaSeleccionada: null,
            toastMessage: "",
            currentPage: 1,
            itemsPerPage: 8,

            usuarioActivo: "sin sesion",
            showUserDropdown: false,  // Controla la visibilidad del dropdown de usuario
            showModal: false,         // Controla la visibilidad del modal
            modalMessage: '',         // Mensaje que se mostrará en el modal
            actionToConfirm: null,    // Acción que se va a confirmar (cerrar sesión/eliminar cuenta)
        };
    },
    computed: {
        displayedOfertas() {
            const start = (this.currentPage - 1) * this.itemsPerPage;
            return this.ofertas.slice(start, start + this.itemsPerPage);
        },
        totalPages() {
            return Math.ceil(this.ofertas.length / this.itemsPerPage);
        }
    },
    methods: {
        toggleUserDropdown() {
            this.showUserDropdown = !this.showUserDropdown;
        },

        // Mostrar el modal para confirmar la acción de cerrar sesión
        confirmLogout() {
            this.modalMessage = '¿Estás seguro que quieres cerrar sesión?';
            this.actionToConfirm = this.logoutAprovechante();  // Establecer la acción que se confirmará
            //this.showModal = true;  // Mostrar el modal
        },
        async deletAprovechante() {
            console.log("llego al delete")
            const confirmacion = window.confirm('¿Estás seguro que quieres eliminar tu cuenta?');
            if (confirmacion) {
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
        }}
        ,
        async logoutAprovechante() {
            try {
                const confirmacion = window.confirm('¿Estás seguro que quieres cerrar sesión?');
                if (confirmacion) {
                    // Realizamos la solicitud POST al backend para cerrar sesión
                    const response = await axios.post(LOGOUT);

                    // Verificamos si la respuesta fue exitosa
                    if (response.status === 200) {
                        alert("Sesión cerrada con éxito");
                        window.location.href = "index.html"; // Redirigir al login o a la página deseada
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

        // Mostrar el modal para confirmar la eliminación de cuenta
        confirmDeleteAccount() {
            this.modalMessage = '¿Estás seguro que quieres eliminar tu cuenta?';
            this.actionToConfirm = this.deletAprovechante();  // Establecer la acción que se confirmará
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

        async cargarOfertas() {
            try {
                const res = await axios.get(API_OFERTAS);
                this.ofertas = res.data;
                console.log(this.ofertas)
            } catch (err) {
                console.error("Error al cargar ofertas", err);
                this.showToast("No se pudieron cargar las ofertas", "bg-danger");
            }
        },
        formatDate(dateStr) {
            return new Date(dateStr).toLocaleDateString('es-ES');
        },
        confirmarOferta(oferta) {
            this.ofertaSeleccionada = oferta;
            new bootstrap.Modal(document.getElementById("confirmModal")).show();
        },
        async aceptarOferta() {
            try {
                await axios.put(`${API_OFERTAS}/aceptar/${this.ofertaSeleccionada.id}`);
                this.showToast("Oferta aceptada correctamente", "bg-success");
                this.cargarOfertas();
            } catch (err) {
                console.error("Error al aceptar la oferta:", err);
                this.showToast("No se pudo aceptar la oferta: " + error, "bg-danger");
            } finally {
                bootstrap.Modal.getInstance(document.getElementById("confirmModal")).hide();
            }
        },
        prevPage() {
            if (this.currentPage > 1) this.currentPage--;
        },
        nextPage() {
            if (this.currentPage < this.totalPages) this.currentPage++;
        },
        goToPage(page) {
            this.currentPage = page;
        },
        showToast(msg, bgClass) {
            this.toastMessage = msg;
            const toastEl = document.querySelector(".toast");
            toastEl.className = `toast custom-toast text-white ${bgClass} show`;
            const toast = new bootstrap.Toast(toastEl);
            toast.show();
            setTimeout(() => this.toastMessage = "", 3000);
        }
    },
    mounted() {
        this.cargarOfertas();
        this.getActiveAprovechante();
    }
}).mount("#app");
