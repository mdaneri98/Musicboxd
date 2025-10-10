<ClaudeConfiguration>
  <Project name="Musicboxd_API_Migration">
    <Description>
      Migración del sistema Musicboxd (Letterboxd para música) desde una arquitectura Spring MVC con Hibernate hacia una API REST pura,
      que sirva datos JSON a una SPA independiente. Este documento define las pautas de trabajo y el comportamiento esperado
      para un agente de IA colaborando en el proyecto.
    </Description>
    <Directives>
      <Directive>ALWAYS read PLANNING.md at the start of every new conversation or session.</Directive>
      <Directive>CHECK TASKS.md before starting any work.</Directive>
      <Directive>MARK completed tasks immediately after finishing them.</Directive>
      <Directive>ADD new tasks to TASKS.md every single time a new one arises.</Directive>
      <Directive>FOLLOW the PRD (Product Requirements Document) strictly unless explicitly told otherwise.</Directive>
      <Directive>KEEP consistency between layers (Persistence, Service, API).</Directive>
      <Directive>ENSURE responses are JSON-serializable and conform to API schema.</Directive>
    </Directives>
    <Environment>
      <Language>Java 21</Language>
      <Framework>Spring MVC / Spring Web (REST)</Framework>
      <Server>Tomcat 9</Server>
      <Database>PostgreSQL</Database>
      <ORM>Hibernate</ORM>
      <BuildSystem>Maven</BuildSystem>
      <Documentation>Swagger</Documentation>
      <Authentication>JWT</Authentication>
    </Environment>
    <Workflow>
      <Phase name="Planning">
        <Step>Read PLANNING.md</Step>
        <Step>Check TASKS.md</Step>
        <Step>Confirm assigned task scope</Step>
      </Phase>
      <Phase name="Execution">
        <Step>Modify code or documentation as needed</Step>
        <Step>Update TASKS.md after completion or when new tasks appear</Step>
        <Step>Maintain coherence with current API and DTO structure</Step>
      </Phase>
      <Phase name="Review">
        <Step>Ensure task goals meet PRD requirements</Step>
        <Step>Validate Swagger documentation consistency</Step>
        <Step>Confirm REST responses align with expected JSON schema</Step>
      </Phase>
    </Workflow>
    <Safety>
      <Rule>Do NOT modify database schema without prior approval.</Rule>
      <Rule>Do NOT expose sensitive data in API responses.</Rule>
      <Rule>Respect transactional integrity in the Service layer.</Rule>
      <Rule>Preserve backward compatibility with existing DTOs.</Rule>
    </Safety>
    <OutputStandards>
      <Format>UTF-8</Format>
      <LineEndings>LF</LineEndings>
    </OutputStandards>
    <FilesToMonitor>
      <File>PLANNING.md</File>
      <File>TASKS.md</File>
      <File>api/src/main/java/ar/edu/itba/paw/api/**</File>
      <File>pom.xml</File>
    </FilesToMonitor>
  </Project>
</ClaudeConfiguration>
