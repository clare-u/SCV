"use client";

import Button from "@/components/button/Button";
import Badge from "@/components/badge/Badge";

export default function Home() {
  return (
    <div className="flex flex-col gap-10 p-20">
      {/* Indigo fill 버튼 */}
      <div className="flex gap-10">
        <Button size="l" design="fill" color="indigo" icon="add_box">
          Indigo Fill Large
        </Button>
        <Button size="m" design="fill" color="indigo" icon="add_box">
          Indigo Fill Medium
        </Button>
        <Button size="s" design="fill" color="indigo" icon="add_box">
          Indigo Fill Small
        </Button>
      </div>

      {/* Green fill and outline 버튼 */}
      <div className="flex gap-10">
        <Button size="m" design="fill" color="green" icon="add_box">
          Green Fill
        </Button>
        <Button size="m" design="outline" color="green" icon="add_box">
          Green Outline
        </Button>
      </div>

      {/* Disabled Button */}
      <div className="flex gap-10">
        <Button size="m" design="fill" color="red" disabled icon="add_box">
          Disabled Button
        </Button>
      </div>

      {/* Badge */}
      <div className="flex gap-10">
        <Badge color="red">Badge</Badge>
        <Badge color="gray">Badge</Badge>
        <Badge color="green">Badge</Badge>
        <Badge color="blue">Badge</Badge>
        <Badge color="amber">Badge</Badge>
        <Badge color="teal">Badge</Badge>
      </div>
    </div>
  );
}
