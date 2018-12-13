/******************************************************************************/
/*!
\file   RenderPoint.cpp
\author taehee.gweon
\par    email: kth658\@gmail.com
\par    CS180
\par    Assignment #1
\date   20/10/2016
\brief
 This is program trying to speed up the rendering by rewriting portions
 of the above code using inline assembler to resolve bottleneck.
*/
/******************************************************************************/
#include "RenderPoint.h"

//! Namespace for Defined values
namespace 
{
  const double FOUR = 4.0;//!< To use the value 4 with float registers
}

/*!
\param norm2, a, b replace to each st(0), st(1), st(2)
\brief After running loop end, st(0), st(1), st(2) will remain
 each (a*a + b*b), (a*a - b*b + x), (2.0*a*b + y). then, again running loop.
*/
COLORREF RenderPoint(double x, double y, int N) 
{
	int n; //used to store iteration count once the loop is finished.

  __asm
  {
    //All student code must go here
	  fldz;				
	  fldz;
	  fldz;					//!< st. norm2, a, b
	  mov n, 0;

  TOP:
	  fld FOUR;				//!< st. 4.0, norm2, a, b
	  fcomp st(1);			//!< compare 4.0 with norm2
	  fnstsw ax;
	  and ax, 0x4100;
	  cmp ax, 0x0100;
	  jne CMP_FIRST;		//!< if !(norm2 < 4.0), go next comparing
	  jmp DONE;
	  
  CMP_FIRST:
	  mov eax, n;
	  cmp eax, N;
	  jl RUN;				//!< if n < N, execute run loop
	  jmp DONE;

  RUN:
	  fld st(2);			//!< st. b, norm2, a, b
	  fmul st(0), st(0);	//!< st. b*b, norm2, a, b
	  fld st(2);			//!< st. a, b*b, norm2, a, b
	  fmul st(0), st(0);	//!< st. a*a, b*b, norm2, a, b
	  fld st(0);			//!< st. a*a, a*a, b*b, norm2, a, b
	  fsub st(0), st(2);	//!< st. (a*a - b*b), a*a, b*b, norm2, a, b
	  fld x;				//!< st. x, (a*a - b*b), a*a, b*b, norm2, a, b
	  faddp st(1), st(0);	//!< st. (a*a - b*b + x), a*a, b*b, norm2, a, b
	  fld1;					//!< st. 1.0, (a*a - b*b + x), a*a, b*b, norm2, a, b
	  fadd st(0), st(0);	//!< st. 2.0, (a*a - b*b + x), a*a, b*b, norm2, a, b
	  fmulp st(6), st(0);	//!< st. (a*a - b*b + x), a*a, b*b, norm2, a, 2.0*b
	  fld st(4);			//!< st. a, (a*a - b*b + x), a*a, b*b, norm2, a, 2.0*b
	  fmulp st(6), st(0);	//!< st. (a*a - b*b + x), a*a, b*b, norm2, a, 2.0*a*b
	  fld y;				//!< st. y, (a*a - b*b + x), a*a, b*b, norm2, a, 2.0*a*b
	  faddp st(6), st(0);	//!< st. (a*a - b*b + x), a*a, b*b, norm2, a, (2.0*a*b + y)
	  fstp st(4);			//!< st. a*a, b*b, norm2, (a*a - b*b + x), (2.0*a*b + y)
	  faddp st(1), st(0);	//!< st. (a*a + b*b), norm2, (a*a - b*b + x), (2.0*a*b + y)
	  fstp st(1);			//!< st. (a*a + b*b), (a*a - b*b + x), (2.0*a*b + y)
							//!  | norm2 = a*a + b*b | a = (a*a - b*b + x(equal to c)) | b = 2.0*a*b + y
	  inc n;
	  jmp TOP;

  DONE:
	  fstp st(0);
	  fstp st(0);
	  fstp st(0);			//!< Make sure to clear the stack
  }

  x = double(n)/N;
  int value = int(255*(1 - x));
  return RGB(value,value,value);

}
