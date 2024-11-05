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

const CloneModal = () => {
  const [isOpen, setIsOpen] = useState(false);

  // 인풋
  const [inputValue, setInputValue] = useState("기존 모델명" + " - 사본");

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setInputValue(e.target.value);
  };

  // 라디오
  const dataset = ["MNIST", "Fashion", "CIFAR-10", "SVHN", "EMNIST"];
  const [selected, setSelected] = useState(dataset[0]);

  // 새로 만들기
  const handleCreate = () => {
    if (!inputValue.trim()) {
      toast.error("모델명을 입력해주세요.");
    } else {
      alert(`모델명: ${inputValue}\n데이터셋: ${selected}`);
    }
  };

  return (
    <>
      <Button
        size="l"
        design="fill"
        color="indigo"
        icon="content_copy"
        onClick={() => setIsOpen(true)}
      >
        사본 만들기
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
              <div className="p-10">사본 만들기</div>
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
                  icon="content_copy"
                  onClick={handleCreate}
                >
                  사본 만들기
                </Button>
              </div>
            </Description>
          </DialogPanel>
        </div>
      </Dialog>
    </>
  );
};

export default CloneModal;
