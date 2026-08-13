// * is an indicator used by Tailwind to say "for all direct children"
// generic: [&_ *] means "for every children (at any level) placed under this tag"
// specific: [&_form_*] means "every element inside form tag, but not direct children of this"

//////////////////////////////////////// BACKGROUND COLORS ////////////////////////////////////////

// for dark mode?
export const PASTEL_GRAY: string = "bg-[#CCCCCC]";

export const WHITE: string = "bg-white";

// pastel pink background for pink mode
export const PASTEL_PINK: string = "bg-[#F0A6CA]";
// tab divider color, outline/border color if pink mode
export const PASTEL_BLUE: string = "bg-[#93C5FD]";

// default background color
export const LAVENDER: string = "bg-[#A78BFA]";

//////////////////////////////////////// BACKGROUND COLORS ////////////////////////////////////////

//////////////////////////////////////// CONTAINER ////////////////////////////////////////

// rounded corners, width and height to fill the given size by parent, center items in container
export const CONTAINER_CLASS_NAME: string =
  "rounded-[2rem] tracking-widest font-bold shadow-[var(--theme-shadow)]";

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

//////////////////////////////////////// INPUT ////////////////////////////////////////

// rounded corners, focus:scale-105 to slightly enlarge the input when focused
// transition-transform duration-150 makes the pop-out animation smooth instead of instantaneous
// text position, text color, border color, and where to place the input must be provided by parent
export const INPUT_CLASS_NAME: string =
  "rounded-[3rem] focus:scale-105 focus:outline-none border-2 border-transparent" +
  "focus:shadow-[0_6px_12px_rgba(124,58,237,0.25)] transition-transform duration-150" +
  "bg-[var(--theme-input-background)] text-[var(--theme-input-text)]";

//////////////////////////////////////// INPUT ////////////////////////////////////////

//////////////////////////////////////// BUTTON ////////////////////////////////////////

export const BUTTON_CLASS_NAME: string = "";

//////////////////////////////////////// BUTTON ////////////////////////////////////////

//////////////////////////////////////// Spinner (loading symbol) ////////////////////////////////////////

// inline-block : reliably give dimensions like size-5. without this, size-5 will behave differently
// size-5 : sets both width and height to 5 (20px)
// animate-spin : Tailwind's built-in spinner animation
// rounded-full : makes the shape completely round (circle)
// border-2 : creates a 2px border around the circle
// border-white : makes the entire 2px border white
// border-t-transparent : makes the top portion of the border transparent
export const SPINNER_CLASS_NAME: string =
  "inline-block size-5 animate-spin rounded-full border-2 border-white border-t-transparent";

//////////////////////////////////////// Spinner (loading symbol) ////////////////////////////////////////

//////////////////////////////////////// TAB ////////////////////////////////////////

// tab bar also uses CONTAINER_CLASS_NAME
export const TAB_BAR_CLASS_NAME: string =
  "flex h-[5vh] items-center bg-[var(--theme-background)]";

// parent must have fixed height
export const TAB_BAR_DIVIDER_CLASS_NAME: string =
  "mx-3 h-[30px] w-[3px] bg-[var(--theme-accent)]";

export const TAB_CLASS_NAME: string = "px-6 py-3 font-bold transition-colors";

export const TAB_SELECT_CLASS_NAME: string =
  "relative after:absolute after:bottom-0 after:left-1/2 " +
  "after:-translate-x-1/2 after:w-[60%] after:border-b-4 after:border-b-[var(--theme-border)]";

//////////////////////////////////////// TAB ////////////////////////////////////////

//////////////////////////////////////// CONTENT ////////////////////////////////////////

// w-full: fills in the full width provided by home page, height naturally grows with children
// px and py: give padding on horizontal and vertical sides
export const CONTENT_CLASS_NAME: string =
  "w-full px-6 py-2 bg-[var(--theme-background)] text-2xl font-semibold";

// To have multiple Container components in one row
export const CONTENT_ROW_CLASS_NAME: string = "";

// To have multiple Container components in a column (in one row)
export const CONTENT_COLUMN_CLASS_NAME: string = "";

//////////////////////////////////////// CONTENT ////////////////////////////////////////

//////////////////////////////////////// LOGIN FORM ////////////////////////////////////////

// (#CCCCCC gray background) lavender background, white text, shadow to pop out
export const LOGIN_CONTAINER_CUSTOM_CLASS_NAME: string =
  "bg-[radial-gradient(circle,#A78BFA_45%,#A08FEE_100%)] text-white shadow-x1";

export const LOGIN_FORM_CLASS_NAME: string =
  "flex flex-col items-center gap-8 px-6 py-10";

// p-3 adds padding to all four sides (12px each)
// border-2 border-transparent focus:outline-none removes the default browser's input outline (black outline)
export const LOGIN_FORM_INPUT_CLASS_NAME: string =
  "p-3 rounded-[3rem] text-center text-[#FFF7D6] " +
  "border-2 border-transparent focus:outline-none focus:border-[#FFF7D6] " +
  "focus:scale-105 transition-transform duration-750";

//////////////////////////////////////// LOGIN FORM ////////////////////////////////////////

export const MAIN_CLASS_NAME: string =
  "flex flex-col items-center gap-3 pt-5 w-[90vw] mx-auto";
