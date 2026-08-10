// * is an indicator used by Tailwind to say "for all direct children"
// generic: [&_ *] means "for every children (at any level) placed under this tag"
// specific: [&_form_*] means "every element inside form tag, but not direct children of this"

//////////////////////////////////////// BACKGROUND COLORS ////////////////////////////////////////

export const PASTEL_GRAY: string = "bg-[#CCCCCC]";

//////////////////////////////////////// BACKGROUND COLORS ////////////////////////////////////////

//////////////////////////////////////// CONTAINER ////////////////////////////////////////

// rounded corners, width and height to fill the given size by parent, center items in container
export const CONTAINER_CLASS_NAME: string =
  "rounded-[2rem] w-full tracking-widest font-bold shadow-[0_10px_25px_rgba(0,0,0,0.22)]";

// relative allows title to be shifted
// pt-25: how much padding from top border. greater the number, greater the offset
// text-center = horizontally centered
// text-x1 slightly larger since it's a title
// font-bold makes it bold
// tracking-wide gives slightly more spacing between letters
export const CONTAINER_TITLE_CLASS_NAME: string =
  "relative pt-25 text-center text-4xl";

// outline gives every child object an outline
// -x where x is a single digit number, define thickness of outline
// outline-green-500 makes this outline green
export const CONTAINER_CHILD_CLASS_NAME: string =
  "flex flex-col items-center gap-8 px-6 py-10";

//////////////////////////////////////// CONTAINER ////////////////////////////////////////

// (#CCCCCC gray background) lavender background, white text, shadow to pop out
export const LOGIN_CONTAINER_CUSTOM_CLASS_NAME: string =
  "bg-[radial-gradient(circle,#A78BFA_45%,#A08FEE_100%)] text-white shadow-x1";

export const LOGIN_FORM_CLASS_NAME: string =
  "flex flex-col items-center gap-8 px-6 py-10";

// p-3 adds padding to all four sides
// transition-transform duration-150 makes the pop-out animation smooth instead of instantaneous
// focus:scale-105 slightly enlarges the input when focused
// focus:outline-none removes the default browser's input outline (black outline)
export const LOGIN_FORM_INPUT_CLASS_NAME: string =
  "p-3 rounded-[3rem] text-center text-[#FFF7D6] transition-transform duration-150" +
  " focus:scale-105 focus:outline-none border-2 border-transparent focus:border-[#FFF7D6]" +
  " focus:shadow-[0_6px_12px_rgba(124,58,237,0.25)]";
