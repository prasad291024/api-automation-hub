# BaseTest Refactor Plan

## Goal
Refactor BaseTest to eliminate direct RequestSpecification manipulation in favor of service layer usage.

## Current State Analysis
The current BaseTest.java has:
- Service objects initialized (bookingService, authService)
- Backward compatibility code maintaining RequestSpecification usage
- TODO comment indicating RequestSpecification should be removed
- Helper methods delegating to service objects (createBooking, getBooking, etc.)
- Configuration and utility initialization

## Issues to Address
1. Remove RequestSpecification-related fields and initialization
2. Eliminate backward compatibility code
3. Ensure clean service-based initialization
4. Maintain all existing functionality through service delegation
5. Keep helper methods that delegate to services (these are good)

## Proposed Changes
1. Remove these fields:
   - protected RequestSpecification requestSpecification;
   - protected io.restassured.response.Response response;
   - protected io.restassured.response.ValidatableResponse validatableResponse;

2. Remove RequestSpecification initialization from setUp() method
3. Remove the backward compatibility TODO comment
4. Keep all service delegation methods (createBooking, getBooking, etc.) as they are good abstractions
5. Keep configuration and utility initialization as they are needed
6. Keep helper methods like getBaseUrl() and getToken() as they provide clean abstraction

## Files to Modify
- restful-booker/src/test/java/com/prasad_v/tests/base/BaseTest.java

## Verification Steps
1. Ensure BaseTest compiles after changes
2. Verify TestCreateBookingImproved still works (it uses service delegation methods)
3. Verify existing tests that might use RequestSpecification still work or need updating
4. Run a subset of tests to ensure no regression

## Benefits
- Cleaner BaseTest focused on service layer abstraction
- Eliminates mixed abstraction levels
- Better separation of concerns
- Aligns with improvement plan goals
- Prepares for full service-layer adoption across all tests