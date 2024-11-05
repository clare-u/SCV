"use client";

import SidebarItem from "./SidebarItem";

const Sidebar = () => {
  return (
    <nav className="fixed left-0 flex h-full w-[300px] flex-col items-center border-r-2 border-gray-500 bg-stone-200">
      <SidebarItem href="/docs/convolution-layers">
        Convolution Layers
      </SidebarItem>
      <SidebarItem href="/docs/pooling-layers">Pooling Layers</SidebarItem>
      <SidebarItem href="/docs/padding-layers">Padding Layers</SidebarItem>
      <SidebarItem href="/docs/non-linear-activations">
        Non-Linear Activations
      </SidebarItem>
      <SidebarItem href="/docs/linear-layers">Linear Layers</SidebarItem>
    </nav>
  );
};

export default Sidebar;
