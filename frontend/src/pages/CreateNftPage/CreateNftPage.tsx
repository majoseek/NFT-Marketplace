import { useState } from 'react';

const CreateNftPage = () => {
    const [name, setName] = useState('');
    const [description, setDescription] = useState('');
    const [file, setFile] = useState<File | null>(null);
    const canCreateNft = name && description && file;

    const handleNameChange = (e: React.ChangeEvent<HTMLInputElement>) =>
        setName(e.target.value);

    const handleDescChange = (e: React.ChangeEvent<HTMLInputElement>) =>
        setDescription(e.target.value);

    const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        if (!event.target.files) return;
        event.target.files[0] && setFile(event.target.files[0]);
    };

    const handleSubmit = async () => {
        if (!name || !description || !file) return;
        const formData = new FormData();
        formData.append('file', file);
        formData.append('name', name);
        formData.append('description', description);
    };

    return true ? (
        <main className="py-32 px-20 flex items-start flex-col justify-center">
            <div className="flex justify-around w-full">
                <span>
                    <h3 className="text-4xl font-bold">Create your own item</h3>
                    <h4 className="text-xl mt-3">
                        Just type in name, description and attach file
                    </h4>
                </span>
            </div>
            <section className="flex gap-10 mt-16 flex-wrap justify-center w-full">
                <div className="flex flex-col gap-6">
                    <input
                        type="text"
                        placeholder="Enter name..."
                        className="input input-bordered w-96"
                        value={name}
                        onChange={handleNameChange}
                    />
                    <input
                        type="text"
                        placeholder="Enter description..."
                        value={description}
                        className="input input-bordered w-96"
                        onChange={handleDescChange}
                    />
                    <input
                        type="file"
                        className="file-input w-full max-w-xs border-white"
                        onChange={handleFileChange}
                    />
                    <button
                        className="btn btn-primary disabled:border-white"
                        disabled={!canCreateNft}
                        onClick={handleSubmit}
                    >
                        CREATE
                    </button>
                </div>
            </section>
        </main>
    ) : (
        <progress className="progress w-56" />
    );
};

export default CreateNftPage;
