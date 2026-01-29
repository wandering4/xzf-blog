import axios from "@/axios";


// 获取所有角色
export function getAllRoles() {
    return axios.get("/user/role/getAll")
}
