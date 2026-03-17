import Image from "next/image";
import React from "react";
import { Button } from "@/components/ui/button";

const Promote = () => {
  return (
    <div className="w-full h-[812px] relative">
      <Image
        src="/images/background.png"
        alt="Promote"
        width={1440}
        height={800}
        className="w-full h-full object-cover"
      />
      <div className="absolute top-50 right-20 w-[640px] h-[400px] bg-orange-100 text-black rounded-lg p-6 flex flex-col gap-6">
        <h6 className="text-xl font-bold">new arrival</h6>
        <h2 className="text-5xl font-bold text-yellow-700">
          Discover Our <br /> New Collection
        </h2>
        <p className="text-xl">
          Lorem ipsum dolor sit amet consectetur, adipisicing elit. Sed ex totam
          quo repellat voluptatum quia consectetur aperiam. Deleniti, delectus
          molestiae velit pariatur dolor odio sed quidem inventore perferendis
          ullam quos?
        </p>
        <Button
          variant="default"
          className="p-4 rounded-none w-40 h-12 text-xl bg-yellow-700 text-white font-bold"
        >
          Shop Now
        </Button>
      </div>
    </div>
  );
};

export default Promote;
