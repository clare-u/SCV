import MonacoEditor from "@monaco-editor/react";

interface CodeViewerProps {
  codeString: string;
}

const CodeViewer: React.FC<CodeViewerProps> = ({ codeString }) => (
  <MonacoEditor
    defaultLanguage="python"
    theme="vs-dark"
    value={codeString}
    options={{
      readOnly: true,
      lineNumbers: "on",
      wrappingIndent: "same",
      wordWrap: "on", // 긴 줄을 자동으로 줄바꿈
      minimap: { enabled: false },
    }}
  />
);

export default CodeViewer;
