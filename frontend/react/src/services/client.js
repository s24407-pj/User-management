import axios from "axios";

export const getCustomers = async () => (

    await axios.get(`${import.meta.env.VITE_API_BASE_URL}/api/v1/customers`)
)


export const saveCustomer = async (customer) => (

    await axios.post(
        `${import.meta.env.VITE_API_BASE_URL}/api/v1/customers`,
        customer)
)


export const deleteCustomer = async (id) => (
    await axios.delete(
        `${import.meta.env.VITE_API_BASE_URL}/api/v1/customers/${id}`)
)


export const updateCustomer = async (id, update) => (
    await axios.put(
        `${import.meta.env.VITE_API_BASE_URL}/api/v1/customers/${id}`, update)
)