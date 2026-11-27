package tn.esprit.tpfoyer17.configurations;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Aspect
@Slf4j
public class AOPConfig {

	@Around("execution(* tn.esprit.tpfoyer17.services.EtudiantService.*(..))")
	public Object temps(ProceedingJoinPoint pjp) throws Throwable {
		long start = System.currentTimeMillis();
		try {
			return pjp.proceed();
		} finally {
			long time = System.currentTimeMillis() - start;
			log.info("{} exécuté en {} ms", pjp.getSignature().getName(), time);
		}
	}
}