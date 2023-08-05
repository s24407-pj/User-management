import axios from "axios";

export const getCustomers = async () => {
    try {
        await axios.get("http://localhost:8080/api/v1/customers")
    } catch (e) {
        throw e;
    }
}