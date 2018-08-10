# Plans

## Generic client-facing ORM

- All permission-based - you can only modify things you're supposed to, but you can modify freely anything you're allowed to.
- Works with multiple databases under the hood using interfaces
- User can send queries with `ConditionOnItem`
- User can send modifications with `ModificationOnItem`
- User can observe changes on items?  Be careful with running multiple servers!

## Authorization

- `HasEmailPassword` interface for conveniently implementing password reset
- `HasEmail` interface for implementing SSO login?
- `HasPassword` interface for handling hashing and checking and changing of passwords
- `HasOAuthData` interface for handling OAuth logins

All of these have to attach to a generic database interface

## Admins

Built in concepts somehow?

What permissions does this user have?