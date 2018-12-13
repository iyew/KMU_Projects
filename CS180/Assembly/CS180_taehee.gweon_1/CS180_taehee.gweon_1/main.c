/******************************************************************************
filename    main.c
author      Matt Casanova
DP email    mcasanov@digipen.edu
course      CS102
assignment  5
due date    12/11/2016

Brief Description:
This file contains the main function and function calls for assignment 5 in
cs102.

******************************************************************************/
#define _CRT_SECURE_NO_WARNINGS

#include <stdio.h>
#include <string.h>
#include <time.h>

/*function prototypes*/
int Fibonacci(int value);
int Factorial(int value);
int NextPowerOf2(int value);
int GCD(int a, int b);
void ConcatString(const char* source, char* desination);
void ReverseString(char* string);

static void MyReverseString(char* string)
{
  int backIndex = 0;
  int frontIndex = 0;
  char swapStorage;

  while(string[backIndex] != 0)
    ++backIndex;

  --backIndex;
  while(frontIndex <= backIndex)
  {
    swapStorage = string[frontIndex];
    string[frontIndex] = string[backIndex];
    string[backIndex] = swapStorage;
    ++frontIndex; 
    --backIndex;
  }
}

#define MAX_FIB 20
#define MAX_FACT 12
#define MAX_POW 17
#define MAX_GCD 11 
#define MAX_WORDS 10
#define MAX_LENGTH 128

const char* firstWords[MAX_WORDS] = {
  "Hello",
  "I'm a little",
  "Han",
  "Batman",
  "Matt",
  "It's a",
  "Knock",
  "Who's",
  "Fried green",
  "James T"
};
const char* secondWords[MAX_WORDS] = {
  " World",
  " teapot",
  " Solo",
  "and Robin",
  " Casanova",
  " small world",
  "Knock",
  " there",
  " tomotoes",
  " Kirk"
};
const char* complete[MAX_WORDS] = {
  "Hello World",
  "I'm a little teapot",
  "Han Solo",
  "Batman and Robin",
  "Matt Casanova",
  "It's a small world",
  "Knock Knock",
  "Who's there",
  "Fried green tomatoes",
  "James T Kirk"
};

char declaration[] = "When in the Course of human events it becomes necessary " 
                     "for one people to dissolve the political bands which "
                     "have connected them with another and to assume among "
                     "the powers of the earth, the separate and equal station "
                     "to which the Laws of Nature and of Nature's God entitle "
                     "them, a decent respect to the opinions of mankind "
                     "requires that they should declare the causes which impel "
                     "them to the separation.";

void Test1(void)
{
  int i;
  printf("Test Fibonocci ======================================================\n\n");

  for(i = 0; i < MAX_FIB; ++i)
  {
    printf("Fibonacci of %d is %d\n", i, Fibonacci(i));
  }

  printf("\n\n");
}

void Test2(void)
{
  int i;
  printf("Test Factorial ======================================================\n\n");

  for(i = 0; i < MAX_FACT; ++i)
  {
    printf("Factorial of %d is %d\n", i, Factorial(i));
  }

  printf("\n\n");
}
void Test3(void)
{
  int i;
  printf("Test Power of 2 =====================================================\n\n");

  for(i = 0; i < MAX_POW; ++i)
  {
    printf("Next Power of 2 after %d is %d\n", i, NextPowerOf2(i));
  }

  printf("\n\n");

}

void Test4(void)
{
  int i;
  int firstNumbers[MAX_GCD] =  {48, 18, 12,  8, 54, 42, 135, 3, 129, 0, 0};
  int secondNumbers[MAX_GCD] = {18, 48,  8, 12, 24, 56,  25, 5,   0, 0, 0};
  printf("Test GCD ============================================================\n\n");

  for(i = 0; i < MAX_GCD; ++i)
  {
    printf("The GCD of %3d and %3d is %2d\n", firstNumbers[i], 
      secondNumbers[i], 
      GCD(firstNumbers[i], secondNumbers[i]));
  }

  printf("\n\n");

}

void Test5(void)
{
  int i;
  char buffer[MAX_LENGTH] = {0};

  printf("Test ConcatString ===================================================\n\n");

  for(i = 0; i < MAX_WORDS; ++i)
  {
    strcpy(buffer, firstWords[i]);

    ConcatString(secondWords[i], buffer);
    if(!strcmp(complete[i], buffer))
      printf("Strings are the same\n");
    else
      printf("Strings are different.\n");

    printf("%s + %s = %s\n" , firstWords[i], secondWords[i], buffer);

  }

  printf("\n\n");
}
void Test6(void)
{
  int i;
  char buffer[MAX_LENGTH] = {0};

  printf("Test ReverseString ==================================================\n\n");

  for(i = 0; i < MAX_WORDS; ++i)
  {
    strcpy(buffer, complete[i]);
    ReverseString(buffer);

    printf("%s reversed is: %s\n", complete[i], buffer);
    ReverseString(buffer);

    printf("Reversing again...");
    if(!strcmp(complete[i], buffer))
      printf("Strings are the same\n\n");
    else
      printf("Strings are different.\n\n");

  }

  printf("\n\n");
}
void Test7(void)
{
  int i;
  clock_t startTime;
  clock_t endTime;
  float finalTime;

  printf("Stress test =========================================================\n\n");

  printf("Reverse 10,000,000 Times using Assembly\n");
  startTime = clock();
  for(i = 0; i < 10000000; ++i)
    ReverseString(declaration);
  endTime = clock();

  finalTime = (float)(endTime - startTime) / CLOCKS_PER_SEC;
  printf("Time: %f\n", finalTime);
  printf("%s\n\n", declaration);

  printf("Reverse 10,000,000 Times using C\n");
  startTime = clock();
  for(i = 0; i < 10000000; ++i)
    MyReverseString(declaration);
  endTime = clock();

  finalTime = (float)(endTime - startTime) / CLOCKS_PER_SEC;
  printf("Time: %f\n", finalTime);
  printf("%s\n\n", declaration);

}

int main(void)
{
  Test1();
  Test2();
  Test3();
  Test4();
  Test5();
  Test6();
  Test7();

  return 0;
}