import BlockList from "@/components/block/BlockList";
import CodeViewer from "@/components/code/CodeViewer";
import Button from "@/components/button/Button";

export default function edit() {
  return (
    <div className="flex h-screen flex-1 flex-col">
      <div className="flex h-[8vh] w-full items-center justify-between border-b border-gray-500 px-10">
        제목 영역
        <div className="flex items-center gap-10 px-10">
          <Button size="m" design="outline" color="indigo" icon="save_alt">
            새로운 버전으로 저장
          </Button>
          <Button size="m" design="fill" color="indigo" icon="save">
            저장
          </Button>
          <Button size="m" design="fill" color="green" icon="play_arrow">
            실행
          </Button>
        </div>
      </div>
      <div className="flex flex-1">
        <BlockList />
        <div className="flex w-[600px] flex-col">
          <div className="flex max-h-[450px] w-full flex-1 overflow-y-auto overflow-x-hidden">
            <CodeViewer></CodeViewer>
          </div>
          <div>실행 결과</div>
          <div>테스트</div>
        </div>
      </div>
    </div>
  );
}
