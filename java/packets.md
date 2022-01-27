# HawkEye Protocol

## States

States determine the current status of the communication bond between a given client and server.

### List of states

| State Name | ID   | Notes |
|------------|------|-------|
| Handshake  | 0    | Title |
| Status     | 1    | Title |
| Ready      | 0    | Title |
| 2          | Text | Title |
| Handshake  | 0    | Title |
| 2          | Text | Title |

## Handshaking

### Server Bound

**Handshake (0x00)**

| Field Name       | Field Type | Notes |
|------------------|------------|-------|
| Protocol Version | Int        | Title |
| 2                | Text       | Title |