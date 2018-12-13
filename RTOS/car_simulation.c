#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <fcntl.h>
#include <errno.h>
#include <linux/input.h>
#include <string.h>
#include "tpl_os.h"

/*
** POSIX 키 입력 이벤트 구조체
** 키가 입력되면 다음 구조체에 이벤트 관련 정보 저장됨
**
** struct input_event {
**    struct timval     time;     // 입력 이벤트가 발생한 시간에 대한 스탬프
**    __u16             type;     // 키 이벤트 형식 - 0: key released, 1: key pressed, 2: key repeated
**    __u16             code;     // 키 이벤트 코드
**    __s32             value;    // 키 이벤트 값
** }
*/
struct input_event ev;

// c언어 논리자료형 구현
typedef enum
{
    false,
    true
} bool;

/*
** 변속기 상태 열거형
** GEAR_PARKING:  주차
** GEAR_REVERSE:  후진
** GEAR_NEUTRAL:  중립
** GEAR_DRIVE:    진행
*/
typedef enum transmission
{
    GEAR_PARKING,
    GEAR_REVERSE,
    GEAR_NEUTRAL,
    GEAR_DRIVE
} Transmission;

// 핸들링 상태 열거형
typedef enum handling
{
    LEFT,
    RIGHT
} Handling;

/*
** 변수 목록
** fuel:        연료량
** speed:       현재 자동차 속도
** RPM:         자동차 엔진 분당회전수(Revolutions Per Minute)
** gear_level:  기어 단수
** airbag:      에어백 터짐 유무
*/
int fuel = 100;
int speed;
int RPM;
int gear_level;
bool airbag;

Transmission tm = GEAR_PARKING;
Handling h;

int main(void)
{
    /*
    ** void StartOS(AppModeType AppModeID);
    ** 운영체제가 oil파일에 설정된 Task 및 Alarm을 자동 시작해주도록 함
    **************************************************************************
    ** 순서
    **
    ** 1. StartOS Call
    ** 2. OS에서 초기화 코드 실행
    ** 3. StartupHook 함수(초기화 절차 진행 가능) 실행
    ** 4. OS 커널 실행
    ** 5. 가장 처음 순서 사용자 Task 실행
    */
    StartOS(OSDEFAULTAPPMODE);
    return 0;
}

/*
** DeclareTask(TaskType TaskID);
** 
** 각각의 Task는 .oil 파일 상에 정적으로 선언되며
** 컴파일시 TaskType이라는 식별자를 가짐
** C 프로그램 내에서 사용하기 위해 선언이 필요함
** C 매크로로 정의되어 있음
******************************************************************************
** task_start_car:			   자동차 시동 켜짐
**
** task_check_module:		   자동차 내부 모듈 점검 및 초기화 작업 수행
** └ task_check_fuel:          남은 연료 확인
** └ task_check_engine:        자동차 엔진 점검
** └ task_check_airbag:        자동차 에어백 점검
**
** task_driving:			   주행 시작, 터미널 내의 입력키에 따른 자동차 주행 작업 수행
** └ task_change_transmission: 자동차 변속기 제어
** └ task_handling:            자동차 핸들 제어
** └ task_accel:               자동차 악셀 제어
** └ task_break:               자동차 브레이크 제어
** └ task_change_gear_level:   자동차 기어 단수 제어
**
** task_end_car:			   자동차 시동 꺼짐
*/
DeclareTask(task_start_car);

DeclareTask(task_check_module);
DeclareTask(task_check_fuel);
DeclareTask(task_check_engine);
DeclareTask(task_check_airbag);

DeclareTask(task_driving);
DeclareTask(task_change_transmission);
DeclareTask(task_handling);
DeclareTask(task_accel);
DeclareTask(task_break);
DeclareTask(task_change_gear_level);
DeclareTask(task_end_car);

/*
** DeclareResource(ResourceType ResourceID);
** 
** 각각의 Resource 또한 .oil 파일 상에 정적으로 선언되며
** 컴파일시 ResourceType이라는 식별자를 가짐
** 역시나 C 프로그램 내에서 사용하기 위해 선언이 필요함
** C 매크로로 정의되어 있음
******************************************************************************
** module_initilaize: 자동차 모듈 초기화 접근에 사용할 Resource
** res_speed:         속도값 접근에 사용할 Resource
** res_airbag:        에어백 접근에 사용할 Resource
*/
DeclareResource(module_initialize);
DeclareResource(res_speed);
DeclareResource(res_airbag);

/*
** oil 파일 상에서 AUTOSTART 옵션을 TRUE로 설정해서 Task들 중 가장 처음 실행되는 Task
** task_check_module Task를 활성화시키고 자신을 Terminate 시킴
*/
TASK(task_start_car)
{
    printf("Starting car simulation..\n");
    ActivateTask(task_check_module);
    TerminateTask();
}

/*
** StatusType GetResource(ResourceType ResourceID);
**
** 다음 함수를 통해 ResourceID에 해당하는 Resource를 얻어 critical section에 진입함
** 함수의 결과로 다음 StatusType의 결과 코드를 반환함
**
** E_OK       : 에러 없음
** E_OS_ID    : 유효하지 않은 Resource id, Resource가 없거나 이미 존재함
** E_OS_ACCESS: 다른 Task 혹은 ISR에서 해당 Resource 점유 중
******************************************************************************
** StatusType ReleaseResource(ResourceType ResourceID);
**
** GetResource() 함수로 획득한 Resource를 반환함
** 함수의 결과로 다음 StatusType의 결과 코드를 반환함
**
** E_OK       : 에러 없음
** E_OS_ID    : 유효하지 않은 Resource id, Resource가 없거나 이미 존재함
*/
TASK(task_check_module)
{
    /*
    ** initialize module(fuel, engine, airbag, etc.)
    */

    // fuel check
    printf("fuel check.. ");
    GetResource(module_initialize);
    ActivateTask(task_check_fuel);
    ReleaseResource(module_initialize);

    // engine check
    printf("engine check.. ");
    GetResource(module_initialize);
    ActivateTask(task_check_engine);
    ReleaseResource(module_initialize);

    // airbag check
    printf("airbag check.. ");
    GetResource(module_initialize);
    ActivateTask(task_check_airbag);
    ReleaseResource(module_initialize);

    printf("initialize result: \nfuel=%d, RPM=%d, speed=%d, gear_level=%d, airbag=OK\n", fuel, RPM, speed, gear_level);

    ActivateTask(task_driving);

    TerminateTask();
}

TASK(task_check_fuel)
{
    printf("\nremaining fuel: %d\n", fuel);
    TerminateTask();
}

TASK(task_check_engine)
{
    int i, j;
    RPM = 1000;
    speed = 0;

    // something working simulation
    for (i = 0; i < 10000; i++)
    {
        for (j = 0; j < 10000; j++)
        {
        }
    }
    printf("success\n");
    TerminateTask();
}

TASK(task_check_airbag)
{
    int i, j;
    airbag = FALSE;
    // something working simulation
    for (i = 0; i < 10000; i++)
    {
        for (j = 0; j < 10000; j++)
        {
        }
    }

    printf("success\n");
    TerminateTask();
}

/*
** 자동차 주행 Task
** 시뮬레이션을 위해 POSIX keyboard event을 다루는 procedure 포함되어 있음
**
** 기본 조작
** w:       accel 제어
** s:       break 제어
** a, d:    핸들 제어
** j, k, l: 변속기 제어
*/
TASK(task_driving)
{
    // 다음 경로의 파일에 키보드 이벤트 정보가 있음
    const char *dev = "/dev/input/by-path/platform-i8042-serio-0-event-kbd";
    ssize_t n;
    int fd;

    // 읽기 모드로 파일 오픈
    fd = open(dev, O_RDONLY);
    if (fd == -1)
    {
        fprintf(stderr, "Cannot open %s: %s.\n", dev, strerror(errno));
        return EXIT_FAILURE;
    }

    // 연료가 전부 연소될 때까지 주행
    while (fuel > 0)
    {
        // 키 입력 이벤트 구조체에 저장
        n = read(fd, &ev, sizeof ev);
        if (n == (ssize_t)-1)
        {
            if (errno == EINTR)
                continue;
            else
                break;
        }
        else
        {
            if (n != sizeof ev)
            {
                errno = EIO;
                break;
            }
            
            // 이벤트 타입이 키보드 입력이며, 단일 혹은 반복 입력일 시, 
            if (ev.type == EV_KEY && ev.value >= 1)
            {
                // pressed 'j' or 'k' or 'l'
                if (ev.code == 36 || ev.code == 37 || ev.code == 38)
                    ActivateTask(task_change_transmission);
                // pressed 'a' or 'd'
                if (ev.code == 30 || ev.code == 32)
                    ActivateTask(task_handling);
                // pressed 'w'
                if (ev.code == 17)
                    ActivateTask(task_accel);
                // pressed 's'
                if (ev.code == 31)
                    ActivateTask(task_break);
            }
        }
        fflush(stdout);
    }
    TerminateTask();
}

TASK(task_change_transmission)
{
     // key 'j'
    if (ev.code == 36)
    {
        tm = GEAR_REVERSE;
        printf("\ntransmission: Reverse");
    }
    // key 'k'
    else if (ev.code == 37) 
    {
        tm = GEAR_NEUTRAL;
        printf("\ntransmission: Neutral");
    }
    // key 'l'
    else 
    {
        tm = GEAR_DRIVE;
        printf("\ntransmission: Drive");
    }

    TerminateTask();
}

TASK(task_handling)
{
    if (ev.code == 30)
    {
        h = LEFT;
        printf("\nleft handling..");
    }
    else
    {
        h = RIGHT;
        printf("\nright handling..");
    }
    TerminateTask();
}

TASK(task_accel)
{
    fuel--;
    RPM += 100;

    if (RPM > 4000)
        RPM = 4000;

    switch (tm)
    {
    case GEAR_DRIVE:
        speed += 2;
        break;
    case GEAR_REVERSE:
        speed -= 2;
        if (RPM > 2000)   RPM = 2000;
        if (speed < -20)  speed = -20;
        break;
    }
    printf("\nCurrent speed: %d, RPM: %d", speed, RPM);
    ActivateTask(task_change_gear_level);
    ActivateTask(task_check_fuel);
    TerminateTask();
}

TASK(task_break)
{
    fuel--;
    if (speed >= 0)
    {
        speed -= 1;
        RPM -= 100;
        if (speed < 0)  speed = 0;
        if (RPM < 1000) RPM = 1000;
    }
    printf("\nCurrent speed: %d, RPM: %d", speed, RPM);
    ActivateTask(task_change_gear_level);
    ActivateTask(task_check_fuel);
    TerminateTask();
}

TASK(task_change_gear_level)
{
    if (tm == GEAR_DRIVE)
    {
        if (RPM >= 2500)
            RPM -= 500;
        if (speed > 10 && speed <= 20)
            gear_level = 2;
        else if (speed > 20 && speed <= 30)
            gear_level = 3;
        else if (speed > 30 && speed <= 40)
            gear_level = 4;
        else if (speed > 40 && speed <= 50)
            gear_level = 5;
        else if (speed > 50 && speed <= 60)
            gear_level = 6;
    }
    else
        gear_level = 1;
    printf("\ngear_level: %d", gear_level);
    TerminateTask();
}

/*
** void ShutdownOS(StatusType Error);
**
** OS를 종료하고 에러코드를 알려줌
** 임베디드 플랫폼에서는 인터럽트 비활성화 및 정지
** POSIX 환경에서는 application 종료
*/
TASK(task_end_car)
{
    printf("\nEnding car simulation..\n");
    ShutdownOS(OSDEFAULTAPPMODE);
    TerminateTask();
}

/*
** 에어백 인터럽트 서비스 루틴
** Ctrl+\로 인터럽트 발생시키면 oil에 정의된 ISR 실행
*/
ISR(isr_airbag)
{
    airbag = TRUE;
    printf("\nwarning!! car crash, inflate airbag\n");
    CallTerminateISR2();
}
