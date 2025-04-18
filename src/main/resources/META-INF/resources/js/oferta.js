const { createApp } = Vue;
const API_OFERTAS = "/oferta";
const API_ACEPTAR = "/oferta/aceptar";

createApp({
    data() {
        return {
            ofertas: [],
            ofertaSeleccionada: null,
            toastMessage: "",
            currentPage: 1,
            itemsPerPage: 8
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
                await axios.post(API_ACEPTAR, {
                    ofertaId: this.ofertaSeleccionada.id
                });
                this.showToast("Oferta aceptada correctamente", "bg-success");
                this.cargarOfertas();
            } catch (err) {
                console.error("Error al aceptar la oferta:", err);
                this.showToast("No se pudo aceptar la oferta", "bg-danger");
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
    }
}).mount("#app");
