# Questions

Here are 2 questions related to the codebase. There's no right or wrong answer - we want to understand your reasoning.

## Question 1: API Specification Approaches

When it comes to API spec and endpoints handlers, we have an Open API yaml file for the `Warehouse` API from which we generate code, but for the other endpoints - `Product` and `Store` - we just coded everything directly. 

What are your thoughts on the pros and cons of each approach? Which would you choose and why?

**Answer:**
```txt
Design-First (OpenAPI Spec Generation):
- Pros: Provides a clear "contract" that can be shared with frontend teams and clients before implementation starts. Ensures consistency between documentation and code. Supports automatic generation of client libraries and mocks.
- Cons: Overhead of managing YAML/JSON files. Generated code can sometimes be less flexible or require complex mapping to domain models.
- Best for: Public APIs, team-based development, or large systems where contract stability is critical.

Code-First (Direct Implementation):
- Pros: Faster for rapid prototyping. Easier to use native framework features and custom validation logic without worrying about the spec generator.
- Cons: Documentation (like Swagger) is derived from code and can deviate if annotations are forgotten. Harder for external consumers to "design against" the API before it's live.
- Best for: Small internal services, rapid iterations, or small teams where communication is fluid.

Choice: For a professional enterprise application, I would choose the Design-First approach (OpenAPI) for its scalability and contract-driven nature. It forces developers to think about the API consumer's needs upfront rather than as an after-thought.
```

---

## Question 2: Testing Strategy

Given the need to balance thorough testing with time and resource constraints, how would you prioritize tests for this project? 

Which types of tests (unit, integration, parameterized, etc.) would you focus on, and how would you ensure test coverage remains effective over time?

**Answer:**
```txt
1. Prioritization:
   - Level 1: Core Business Use Cases (Domain logic like archiving, replacing, and capacity validation). These should be heavily unit tested with mocks to ensure logic is correct regardless of the DB state.
   - Level 2: Concurrency and Data Integrity. Given the nature of warehouse management, optimistic locking and transactional boundaries are critical. I would focus on integration tests (IT) that simulate concurrent updates.
   - Level 3: REST API Contracts. Verify that endpoints return correct status codes (404 for missing, 400 for bad input, etc.).

2. Focus:
   - Unit Tests: High-speed, high-coverage for all validation rules.
   - Integration Tests: Testing repository interactions and database constraints using Testcontainers or H2.
   - Concurrency Tests: Stress testing specific race conditions (like archiving while updating stock).

3. Long-term Effectiveness:
   - Automated Coverage Tracking: Integrate tools like JaCoCo into the CI/CD pipeline and fail builds if coverage drops below a threshold (e.g., 80%).
   - Regression Testing: Every bug found should result in a new test case to prevent recurrence.
   - Mutation Testing: Occasionally run mutation tests (like PITest) to ensure the tests are actually catching logical errors, not just hitting lines of code.
```

