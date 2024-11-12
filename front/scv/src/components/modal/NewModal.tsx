"use client";

import {
  Description,
  Dialog,
  DialogBackdrop,
  DialogPanel,
  DialogTitle,
} from "@headlessui/react";
import { useState } from "react";
import Button from "../button/Button";
import ModalInput from "../input/ModalInput";
import DatasetRadio from "../input/DatasetRadio";
import { toast } from "sonner";
import { useCreateModel } from "@/hooks";
import { ModelRequest, Dataset } from "@/types";
import { useRouter } from "next/navigation";

const NewModal = () => {
  const router = useRouter();

  const [isOpen, setIsOpen] = useState(false);

  // 인풋
  const [inputValue, setInputValue] = useState("");

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setInputValue(e.target.value);
  };

  // 라디오
  const dataset = ["MNIST", "Fashion", "CIFAR10", "SVHN", "EMNIST"];
  const [selected, setSelected] = useState(dataset[0]);

  // 새로 만들기
  const { mutate: createModel, isPending } = useCreateModel({
    onSuccess: (data) => {
      // 응답에서 modelId와 versionId를 추출하여 라우팅
      const modelId = data.modelId;
      const versionId = data.modelVersionId;
      router.push(`/edit/${modelId}/${versionId}`);
    },
  });

  const handleCreate = () => {
    if (!inputValue.trim()) {
      toast.error("모델명을 입력해주세요.");
    } else {
      const data: ModelRequest = {
        dataName: selected as Dataset,
        modelName: inputValue,
      };
      createModel(data); // 단순 호출만 수행
    }
  };

  return (
    <>
      <Button
        size="l"
        design="fill"
        color="indigo"
        icon="add_box"
        onClick={() => setIsOpen(true)}
      >
        새로 만들기
      </Button>
      <Dialog
        open={isOpen}
        onClose={() => setIsOpen(false)}
        className="relative z-50"
      >
        <DialogBackdrop className="fixed inset-0 bg-black/30" />
        <div className="fixed inset-0 flex w-screen items-center justify-center">
          <DialogPanel className="rounded-10 border border-gray-400 bg-white">
            <DialogTitle className="flex items-center justify-between rounded-t-10 bg-indigo-800 p-10 text-16 font-semibold text-white">
              <div className="p-10">새로 만들기</div>
              <button
                className="material-symbols-outlined p-10"
                onClick={() => setIsOpen(false)}
              >
                close
              </button>
            </DialogTitle>
            <Description className="flex flex-col p-10 text-16 font-medium">
              <div className="flex flex-col justify-center gap-10 px-20 py-10">
                <div>모델명</div>
                <ModalInput
                  placeholder="모델명을 입력해주세요."
                  value={inputValue}
                  onChange={handleInputChange}
                />
              </div>
              <div className="flex flex-col justify-center gap-10 px-20 py-10">
                <div>데이터셋 선택</div>
                <DatasetRadio
                  options={dataset}
                  selected={selected}
                  onChange={setSelected}
                />
              </div>
              <div className="flex items-center justify-end self-stretch p-20">
                <Button
                  size="l"
                  design="fill"
                  color="indigo"
                  icon="add_box"
                  onClick={handleCreate}
                  disabled={isPending}
                >
                  {isPending ? "로딩 중..." : "새로 만들기"}
                </Button>
              </div>
            </Description>
          </DialogPanel>
        </div>
      </Dialog>
    </>
  );
};

export default NewModal;
