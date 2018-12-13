int Fibonacci(int value)
{
  int total;
  __asm mov edi, value;
 
   __asm
  {
    /*All student code goes here*/
		// If value is one or two, It is returned.
		cmp edi, 0;
		je FIBONACCI_DONE_BY_ONE_OR_TWO;
		cmp edi, 1;
		je FIBONACCI_DONE_BY_ONE_OR_TWO;
		
		/*
			eax : f(n)
			ebx : f(n-1)
			edx : f(n-2)
			ecx : counter
		*/
		mov ebx, 0;
		mov edx, 1;
		mov ecx, 1;

FIBONACCI_TOP:
		// compare counter = value
		cmp ecx, edi;
		je FIBONACCI_DONE;

		// f(n) = f(n-1) + f(n-2)
		mov eax, ebx;
		add eax, edx;

		mov ebx, edx;	// f(n-1) = f(n-2)
		mov edx, eax;	// f(n-2) = f(n)
		inc ecx;		// increment counter
		jmp FIBONACCI_TOP;

FIBONACCI_DONE_BY_ONE_OR_TWO:
		mov eax, edi;

FIBONACCI_DONE:
  }
  
  __asm mov total, eax;
  
  return total;
}

int Factorial(int value)
{
  int total;

  __asm mov edi, value;

  __asm
  {
    /*All student code goes here*/
		// If value is zero, It is returned to one.
		cmp edi, 0;
		je FACTORIAL_DONE_BY_ZERO;

		/*
		eax : value
		ecx : counter
		*/
		mov eax, 1;
		mov ecx, 0;

FACTORIAL_TOP:
		inc ecx;

		// eax *= ecx
		imul ecx;

		// compare counter = value
		cmp ecx, edi;
		je FACTORIAL_DONE;
		jmp FACTORIAL_TOP;

FACTORIAL_DONE_BY_ZERO:
		mov eax, 1;

FACTORIAL_DONE:
  }

  __asm mov total, eax;

  return total;
}

int NextPowerOf2(int value)
{
  int toReturn;

  __asm mov edi, value;

  __asm
  {
    /*All student code goes here*/
		mov eax, 1;

NEXT_POWER_OF_TWO_TOP:
		// compare eax < value
		cmp eax, edi;
		jg NEXT_POWER_OF_TWO_DONE;

		// eax << 1(shift the bits of ebx to the right equal to multiple to 2)
		shl eax, 1;
		jmp NEXT_POWER_OF_TWO_TOP;

NEXT_POWER_OF_TWO_DONE:
  }

  __asm mov toReturn, eax;

  return toReturn;
}

int GCD(int a, int b)
{
  int toReturn;

  __asm mov eax, a;
  __asm mov ebx, b;

  __asm
  {
    /*All student code goes here*/
		// use Euclidean algorithm
		
		mov edx, 0;

		// if a < b, swap a and b
		cmp eax, ebx;
		jl GCD_SWAP;

		// if a = 0 or b = 0, it returns 0
		cmp eax, 0;
		je GCD_DONE_PARAM_ZERO;
		cmp ebx, 0;
		je GCD_DONE_PARAM_ZERO;

		jmp GCD_TOP;

GCD_SWAP:
		mov esi, eax;
		mov eax, ebx;
		mov ebx, esi;

GCD_TOP:
		// if b = 0, return a
		cmp ebx, 0;
		je GCD_DONE;

		// edx contains the remainder(a % b)
		idiv ebx;
		mov eax, ebx; // a = b;
		mov ebx, edx; // b = a % b
		mov edx, 0;	  // remainder set to 0
		jmp GCD_TOP;

GCD_DONE_PARAM_ZERO:
		mov eax, 0;

GCD_DONE:
  }

  __asm mov toReturn, eax;

  return toReturn;
}

void ConcatString(const char* source, char* destination)
{
  __asm mov esi, source;
  __asm mov edi, destination;

  __asm
  {
    /*All student code goes here*/
		/*
		eax : first string(destination) length counter
		ecx : second string(source) length counter
		ebx : temp value for character
		*/
		mov eax, 0;
		mov ebx, 0;
		mov ecx, 0;

CONCATSTRING_TOP:
		// if destination[eax] != '\0' then eax++
		mov bl, byte ptr[edi + eax];
		cmp bl, 0;
		je CONCATSTRING_RUN;

		inc eax;
		jmp CONCATSTRING_TOP;

	CONCATSTRING_RUN:
		// add source string to destination string
		mov bl, byte ptr[esi + ecx];
		mov byte ptr[edi + eax], bl;

		// increment index
		inc ecx;
		inc eax;

		// if end of source string is null string, then exit the loop
		mov bl, byte ptr[esi + ecx];
		cmp bl, 0;
		je CONCATSTRING_DONE;
		jmp CONCATSTRING_RUN;

CONCATSTRING_DONE:
		mov byte ptr[edi + eax], 0;
  }
}

void ReverseString(char* string)
{
  __asm mov esi, string;

  __asm
  {
	  /*All student code goes here*/
		/*
		ecx : string length counter
		edx : temp value for character
		*/
		mov ecx, 0;
		mov edx, 0;

REVERSE_STRING_TOP:
		// if string[ecx] != '\0' then ecx++
		mov bl, byte ptr[esi + ecx];
		cmp bl, 0;
		je REVERSE_STRING_BEFORE;

		inc ecx;
		jmp REVERSE_STRING_TOP;

REVERSE_STRING_BEFORE:
		// ecx pointed to end of string
		dec ecx;

REVERSE_STRING_RUN:
		// if ecx < edx, Reversing string done.
		cmp ecx, edx;
		jle REVERSE_STRING_DONE;

		// swap start character and end character
		mov al, byte ptr[esi + edx];
		mov bl, byte ptr[esi + ecx];
		mov byte ptr[esi + edx], bl;
		mov byte ptr[esi + ecx], al;

		// increment start index and decrement end index
		inc edx;
		dec ecx;
		jmp REVERSE_STRING_RUN;

REVERSE_STRING_DONE:
  }
}