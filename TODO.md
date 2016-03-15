# TODO

1. How design Account with Domain Events?
2. What kind of storage to use?
3. The list of transactions should be paginated
4. The application should requires authentication in order to access account informations
5. Graph


# What I learned

## Hexagonal architecture with Spring Data

In order to use Spring Data the majority of the documentation explain you that you just have create
repositories extending one of the Spring Data's repository interfaces.

The problem is that your repository interface are inside and spring is outside. So according to clean
architecture principle we should not rely on Spring Data repositories.

So in order to get the both:
1. keep your interfaces inside clean
2. in the adapter create a new interface that extends the SpringOne
3. Create an adapter in which you inject the interface extending spring
4. delegates the calls.

## The JPA cost
* You have to create default constructor
Unessessary link


## Persistence choice
In this project I really wanted to follow striclly the hexagonal architecture principles. A direct
consequence is that my entities cannot depend on any JPA infrastracture or Spring Data infrastructure.
I won't use JPA annotations and Spring Data repository interfaces.

