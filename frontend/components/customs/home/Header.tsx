import React from "react";
import Image from "next/image";
import { Heart, Search, ShoppingCart, UserRoundX } from "lucide-react";
import { Button } from "@/components/ui/button";
import Link from "next/link";
const Header = () => {
  return (
    <header className="w-full p-4 fixed bg-white z-50 flex items-center justify-center">
      <div className="w-[calc(100%-100px)] flex justify-between items-center">
        {/* Display logo brand */}
        <div className="logo">
          <Link href="/">
            <Image src="/images/logo.png" alt="Logo" width={120} height={120} />
          </Link>
        </div>
        {/* Display nav menu */}
        <nav>
          <ul className="flex justify-between items-center gap-3">
            <Button variant="link" className="text-black font-bold text-md">
              <Link href="/user">Home</Link>
            </Button>
            <Button variant="link" className="text-black font-bold text-md">
              <Link href="/user/shop">Shop</Link>
            </Button>
            <Button variant="link" className="text-black font-bold text-md">
              <Link href="/user/about">About</Link>
            </Button>
            <Button variant="link" className="text-black font-bold text-md">
              <Link href="/user/contact">Contact</Link>
            </Button>
          </ul>
        </nav>
        {/* Display features icon */}
        <nav>
          <ul className="flex justify-between items-center gap-3">
            <li>
              <Button variant="outline" size="icon" className="border-none">
                <Link href="/user/login">
                  <UserRoundX />
                </Link>
              </Button>
            </li>
            <li>
              <Button variant="outline" size="icon" className="border-none">
                <Search />
              </Button>
            </li>
            <li>
              <Button variant="outline" size="icon" className="border-none">
                <Heart />
              </Button>
            </li>
            <li>
              <Button variant="outline" size="icon" className="border-none">
                <ShoppingCart />
              </Button>
            </li>
          </ul>
        </nav>
      </div>
    </header>
  );
};

export default Header;
