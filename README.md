# SMSWithoutBorders - Android

## Implementations

### DeviceID

**Requirements**

- _secret_key_:- Derived from a DH handshake with Vault. Not used for any form of encryption for messaging sending.
- _phone_number_:- Phone number used in creating Vault account.
- _public_key_:- The Public key used in the DH handshake above.

```python
import hmac
import hashlib

def generate_device_id(secret_key, phone_number, public_key) -> bytes:
    # Combine the phone number and public key
    combined_input = phone_number + public_key

    # Create an HMAC object using the secret key and SHA-256 hash function
    hmac_object = hmac.new(secret_key.encode(), combined_input.encode(), hashlib.sha256)

    # Generate the device ID as a hexadecimal string
    return hmac_object

device_id = generate_device_id(...)
```

**Implementation considerations**

- Proof of number ownership is required to avoid spoofing as another user. OTP via SMS when creating account can be used here.

**Payload structure**
```python
import struct

# Encoding
...
ctLen = len(encrypted_content_bytes)
payload = base64.encode(concat_bytes(struct.pack("<i", ctLen), encrypted_content_bytes, device_id))

# Decoding
...
ctLen = int.from_bytes(payload[0:4])
cipher_text = payload[ctLen:-64]
device_id = payload[-64:]
```
