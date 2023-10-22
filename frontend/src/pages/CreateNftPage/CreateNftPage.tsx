import { useMutation, useQuery } from '@tanstack/react-query';
import axios from 'axios';
import { useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { API_KEYS } from '../../api/API_KEYS';

type Tag = {
    tagId: number;
    name: string;
    selected: boolean;
};

const CreateNftPage = () => {
    const [name, setName] = useState('');
    const [description, setDescription] = useState('');
    const [availableTags, setAvailableTags] = useState<Tag[]>();
    const [file, setFile] = useState<File | null>(null);
    const navigate = useNavigate();
    const selectedTags = useMemo(
        () =>
            availableTags
                ?.filter(({ selected }) => selected)
                .map(({ tagId }) => tagId),
        [availableTags]
    );
    const canCreateNft =
        name && description && file && selectedTags && selectedTags?.length > 0;

    const { mutateAsync } = useMutation(
        [API_KEYS.CREATE_NFT],
        (formData: FormData) => axios.post('/api/nft', formData),
        { onSuccess: () => navigate('/ownedNfts') }
    );
    const { data: tagsResponse } = useQuery(
        [API_KEYS.GET_TAGS],
        () => axios.get<Omit<Tag, 'selected'>[]>('/api/tag').then((res) => res),
        {
            onSuccess: (res) =>
                setAvailableTags(
                    res.data.map((tag) => ({
                        selected: false,
                        tagId: tag.tagId,
                        name: tag.name,
                    }))
                ),
        }
    );

    const handleNameChange = (e: React.ChangeEvent<HTMLInputElement>) =>
        setName(e.target.value);

    const handleDescChange = (e: React.ChangeEvent<HTMLInputElement>) =>
        setDescription(e.target.value);

    const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        if (!event.target.files) return;
        event.target.files[0] && setFile(event.target.files[0]);
    };

    const handleSubmit = async () => {
        if (!name || !description || !file || !selectedTags) return;
        const formData = new FormData();
        selectedTags.forEach((tag) =>
            formData.append('tags[]', tag.toString())
        );
        formData.append('file', file);
        formData.append('name', name);
        formData.append('description', description);
        mutateAsync(formData);
    };

    return tagsResponse ? (
        <main className="py-32 px-20 flex items-start flex-col justify-center">
            <div className="flex justify-around w-full">
                <span>
                    <h3 className="text-4xl font-bold">Create your own item</h3>
                    <h4 className="text-xl mt-3">
                        Just type in name, description and attach file
                    </h4>
                </span>
                <button
                    className="btn btn-primary"
                    disabled={!canCreateNft}
                    onClick={handleSubmit}
                >
                    CREATE
                </button>
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
                        className="file-input w-full max-w-xs"
                        onChange={handleFileChange}
                    />
                    <div className="dropdown">
                        <span className="btn m-1">Select tags</span>
                        <ul className="dropdown-content menu p-2 shadow bg-base-100 rounded-box w-52 flex-nowrap max-h-64 overflow-y-scroll">
                            {availableTags?.map((tag) => (
                                <li key={tag.tagId}>
                                    <label className="label cursor-pointer">
                                        <span className="label-text">
                                            {tag.name}
                                        </span>
                                        <input
                                            type="checkbox"
                                            checked={tag.selected}
                                            onChange={() =>
                                                setAvailableTags(
                                                    availableTags.map((item) =>
                                                        item.tagId === tag.tagId
                                                            ? {
                                                                  name: item.name,
                                                                  tagId: item.tagId,
                                                                  selected:
                                                                      !item.selected,
                                                              }
                                                            : item
                                                    )
                                                )
                                            }
                                            className="checkbox checkbox-primary"
                                        />
                                    </label>
                                </li>
                            ))}
                        </ul>
                    </div>
                </div>
            </section>
        </main>
    ) : (
        <progress className="progress w-56" />
    );
};

export default CreateNftPage;
