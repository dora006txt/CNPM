const PrescriptionUpload = ({ setPrescriptionFile }) => {
    const handleFileChange = (e) => {
        setPrescriptionFile(e.target.files[0]);
    };

    return (
        <div className="form-control mb-4">
            <label className="label">Tải lên đơn thuốc (nếu cần)</label>
            <input
                type="file"
                accept="image/*"
                onChange={handleFileChange}
                className="file-input file-input-bordered"
            />
        </div>
    );
};

export default PrescriptionUpload;