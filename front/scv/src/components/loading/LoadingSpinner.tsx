"use client";

import { useState, useEffect } from "react";
import {
  Description,
  Dialog,
  DialogBackdrop,
  DialogPanel,
  DialogTitle,
} from "@headlessui/react";

interface LoadingSpinnerProps {
  isOpen: boolean;
  setIsOpen: (isOpen: boolean) => void;
}

const loadingMessages = [
  "AI의 눈을 열심히 깜빡이는 중이에요! 👀",
  "당신의 모델이 세상을 보는 방법을 배우는 중입니다 🌎",
  "픽셀들과 대화를 나누는 중이에요... 🎨",
  "AI가 안경을 닦고 있어요. 더 선명하게 볼게요! 🤓",
  "컴퓨터 비전의 마법이 일어나고 있어요 ✨",
  "신경망이 열심히 운동하는 중입니다 💪",
  "이미지의 비밀을 하나씩 해독하고 있어요 🔍",
  "모델이 최선의 결과를 위해 명상 중입니다 🧘",
  "픽셀들을 정성스럽게 분석하는 중이에요 📊",
  "AI가 머신러닝 책을 마지막으로 복습하는 중! 📚",
  "당신의 블록들이 멋진 모델로 변신하고 있어요 🎯",
  "딥러닝의 바다를 헤엄치는 중입니다 🌊",
  "SCV가 열심히 일하고 있어요 💨",
  "신경망이 최적의 가중치를 찾아 여행 중이에요 🧭",
  "AI가 슈퍼컴퓨터와 통화하는 중입니다 📞",
  "모델이 마지막 테스트를 진행하고 있어요 📝",
  "컴퓨터 비전의 렌즈를 조정하는 중입니다 🔧",
  "이미지 특징들을 하나하나 수집하는 중이에요 🎪",
  "모델이 최고의 성능을 위해 준비운동 중! 🎾",
  "AI가 이미지 처리의 신과 상담하는 중입니다 🙏",
  "딥러닝 네트워크가 열심히 계산 중이에요 🧮",
  "모델이 마지막으로 거울을 보며 점검하고 있어요 👀",
  "AI가 결과를 위해 엄청난 연산을 하는 중! 🚀",
  "신경망이 최적의 경로를 탐색하고 있어요 🗺️",
  "컴퓨터 비전의 현미경을 조정하는 중입니다 🔬",
  "모델이 완벽한 결과를 위해 마무리 작업 중이에요 ⚡",
  "이미지 데이터가 신경망을 통과하는 중입니다 🌈",
  "AI가 슈퍼파워를 충전하는 중이에요 🔋",
  "블록들이 하나로 합쳐져 멋진 모델이 되고 있어요 🎨",
  "곧 놀라운 결과를 보여드릴게요! 조금만 기다려주세요 ⭐",
];

const LoadingSpinner: React.FC<LoadingSpinnerProps> = ({
  isOpen,
  setIsOpen,
}) => {
  const [message, setMessage] = useState("");

  useEffect(() => {
    if (isOpen) {
      // 초기 메시지 설정
      setMessage(
        loadingMessages[Math.floor(Math.random() * loadingMessages.length)],
      );

      // 5초마다 메시지 변경
      const intervalId = setInterval(() => {
        let newIndex;
        do {
          newIndex = Math.floor(Math.random() * loadingMessages.length);
        } while (loadingMessages[newIndex] === message); // 같은 메시지가 연속으로 나오는 것을 방지

        setMessage(loadingMessages[newIndex]);
      }, 5000);

      // cleanup 함수로 interval 제거
      return () => clearInterval(intervalId);
    }
  }, [isOpen]);

  return (
    <Dialog
      open={isOpen}
      onClose={() => setIsOpen(false)}
      className="relative z-50"
    >
      <DialogBackdrop className="fixed inset-0 bg-black/30" />
      <div className="fixed inset-0 flex w-screen items-center justify-center">
        <DialogPanel className="rounded-10 border border-gray-400 bg-white px-20">
          <div className="animate-spin-slow fixed left-1/2 top-[46%] h-[100px] w-[100px] -translate-x-1/2 -translate-y-1/2 transform rounded-full border-8 border-gray-300 border-t-gray-800"></div>
          <Description className="mt-[200px] w-[550px] pb-[60px] pt-20 text-center text-24 font-bold">
            {message}
          </Description>
        </DialogPanel>
      </div>
    </Dialog>
  );
};

export default LoadingSpinner;
