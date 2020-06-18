function validateSum(value) {
    document.getElementById("returnButton").disabled = value > orderDTO.residual;
}

function dateFormat(date) {
    return new Date(date).toLocaleString();
}