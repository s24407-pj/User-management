import axios from "axios";

const getAuthConfig = () => ({
    headers: {
        Authorization: `Bearer ${localStorage.getItem("access_token")}`
    }
})

export const getCustomers = async () => (

    await axios.get(
        `${import.meta.env.VITE_API_BASE_URL}/api/v1/customers`,
        getAuthConfig())
)

export const saveCustomer = async (customer) => (

    await axios.post(
        `${import.meta.env.VITE_API_BASE_URL}/api/v1/customers`,
        customer)
)

export const deleteCustomer = async (id) => (
    await axios.delete(
        `${import.meta.env.VITE_API_BASE_URL}/api/v1/customers/${id}`, getAuthConfig())
)

export const updateCustomer = async (id, update) => (
    await axios.put(
        `${import.meta.env.VITE_API_BASE_URL}/api/v1/customers/${id}`, update, getAuthConfig())
)

export const login = async (usernameAndPassword) => (
    await axios.post(
        `${import.meta.env.VITE_API_BASE_URL}/api/v1/auth/login`,
        usernameAndPassword)
)