import axios from "@/axios";

// 上传文件
export function uploadFile(form) {
    return axios.post("/file/upload", form)
}

