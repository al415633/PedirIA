const { createApp } = Vue;

const RETRIEVE_ONE = "/pescados/";
const STOCK_RETRIEVE_ONE = "/pescados/stock/producto/";
const ADD_STOCK = "/pescados/stock";
const API_STOCK = "/pescados/stock/";
const API_HISTORICO = "/pescados/stock/historico/";
const API_PESCADO = "/pescados/";
const VALIDAR = "/pescados/validar";
const API_OFERTAS = "/oferta";

createApp({
    data() {
        return {
            product: {},
            currentStock: [],
            newStock: {
                cantidad: '',
                fechaIngreso: '',
                fechaVencimiento: ''
            },
            toastMessage: '',
            editingStock: { id: null, cantidad: '', fechaIngreso: '', fechaVencimiento: '' },
            sellStockData: { id: null, cantidad: '' },
            historicoStock: [],
            activeTab: "stock",
            editingProduct: { nombre: '', unidad: '', tipoConserva: '' },
            tiposConserva: ["REFRIGERADO", "FRESCO", "CONGELADO", "VIVO"],
            nombreError: '',
            unidadError: '',
            isInvalid: false,
            ofertas: [],
            nuevaOferta: {
                ubicacion: '',
                cantidad: '',
            },
            editingOferta: { ubicacion: '' },
            selectedOffer: {},

            currentPage: 1,  // Página actual
            itemsPerPage: 7, // Número de elementos por página
        };
    },
    computed: {
        // Stock Actual paginado
        sortedCurrentStockPaginated() {
            const start = (this.currentPage - 1) * this.itemsPerPage;
            return this.currentStock.slice(start, start + this.itemsPerPage).map(stock => {
                return {
                    ...stock,
                    cantidadFormateada: this.formatNumber(stock.cantidad) + " " + (this.product.unidad || "") // Agregar formato de cantidad
                };
            });
        },
        // Histórico de Ventas paginado
        sortedCurrentHistoricoPaginated() {
            const start = (this.currentPage - 1) * this.itemsPerPage;
            return this.historicoStock.slice(start, start + this.itemsPerPage).map(historico => {
                return {
                    ...historico,
                    cantidadFormateada: this.formatNumber(historico.cantidad) + " " + (this.product.unidad || "") // Agregar formato de cantidad
                };
            });
        },
        // Total de páginas para Stock Actual
        totalPagesStock() {
            return Math.ceil(this.currentStock.length / this.itemsPerPage);
        },
        // Total de páginas para Histórico de Ventas
        totalPagesHistorico() {
            return Math.ceil(this.historicoStock.length / this.itemsPerPage);
        }
    },
    methods: {
        // Cargar detalles del producto
        loadProductDetails() {
            const params = new URLSearchParams(window.location.search);
            const id = params.get('id');
            axios.get(RETRIEVE_ONE + id)
                .then(response => {
                    this.product = response.data;
                })
                .catch(error => {
                    console.error("Error al cargar detalles del producto:", error);
                });
        },
        // Cargar stock actual
        loadCurrentStock() {
            const params = new URLSearchParams(window.location.search);
            const id = params.get('id');
            axios.get(STOCK_RETRIEVE_ONE + id)
                .then(response => {
                    this.currentStock = response.data;
                    this.activeTab = "stock";
                })
                .catch(error => {
                    console.error("Error al cargar stock actual:", error);
                });
        },
        // Cargar histórico de ventas
        loadHistorico() {
            const id = new URLSearchParams(window.location.search).get('id');
            axios.get(API_HISTORICO + id)
                .then(response => {
                    this.historicoStock = response.data;
                    this.activeTab = "historico";
                })
                .catch(error => console.error("Error al cargar el historial:", error));
        },
        // Cambiar a la página anterior
        prevPage() {
            if (this.currentPage > 1) {
                this.currentPage--;
            }
        },
        // Cambiar a la siguiente página
        nextPage() {
            if (this.currentPage < this.totalPagesStock) {
                this.currentPage++;
            }
        },
        // Ir a una página específica
        goToPage(page) {
            this.currentPage = page;
        },
        // Métodos para la paginación del Histórico y Stock
        formatDate(dateStr) {
            return new Date(dateStr).toLocaleDateString('es-ES', { year: 'numeric', month: '2-digit', day: '2-digit' });
        },
        formatNumber(value) {
            const num = parseFloat(value);
            if (Number.isInteger(num)) {
                return num.toLocaleString('es-ES', { minimumFractionDigits: 0 });
            }
            return num.toLocaleString('es-ES', { minimumFractionDigits: 0, maximumFractionDigits: 3 });
        },
        showToast(message, bgClass) {
            this.toastMessage = message;
            const toastEl = document.getElementById("toastMessage");
            toastEl.className = `toast custom-toast text-white ${bgClass} show`;
            const toast = new bootstrap.Toast(toastEl);
            toast.show();
        }
    },
    mounted() {
        this.loadProductDetails();
        this.loadCurrentStock();
    }
}).mount("#app");
