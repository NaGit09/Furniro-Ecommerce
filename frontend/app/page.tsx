import Browse from "@/components/customs/home/Browse";
import Promote from "@/components/customs/home/Promote";

export default function Home() {
  return (
    <div className="flex min-h-screen items-center justify-center bg-zinc-50 font-sans dark:bg-black">
      <main className=" w-full flex flex-col items-center justify-center gap-4">
        <Promote />
        <Browse />
      </main>
    </div>
  );
}
