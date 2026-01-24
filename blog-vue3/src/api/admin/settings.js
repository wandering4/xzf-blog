import axios from "@/axios";

export function getPictureList(params = {}) {
    return axios.post("/article//blog/settings/advertisement/picture/list", params)
}

export function deletePicture(id) {
    return axios.post("/article//blog/settings/advertisement/picture/delete", {id}, {
        headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
    })
}

export function editPicture(data) {
    return axios.post("/article//blog/settings/advertisement/picture/edit", data, {
        headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
    })
}