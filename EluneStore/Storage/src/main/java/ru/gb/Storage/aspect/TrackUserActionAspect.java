package ru.gb.Storage.aspect;

import lombok.extern.java.Log;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

@Aspect
@Component
@Log
public class TrackUserActionAspect {
    /**
     * Аспект, который по аннотации @TrackUserAction засекает время выполнения метода и
     * записывает информацию по методу в лог файл (юзера; название метода; параметры метода; время)
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("@annotation(TrackUserAction)")
    public Object getTimeRun(ProceedingJoinPoint joinPoint) throws Throwable {
        Long start = System.currentTimeMillis();

        Object returnedByMethod = joinPoint.proceed();

        Long finish = System.currentTimeMillis();
        long result = finish - start;

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        log.info( "\n--------------------------------------------\n" +
                "Пользователь: " + SecurityContextHolder.getContext().getAuthentication().getName() +
                "\nМетод: " + method.getName() +
                "\nПараметры: " + Arrays.toString(joinPoint.getArgs()) +
                "\nВремя (мс): " + result +
                "\n--------------------------------------------");

        return returnedByMethod;
    }
}
