# SMSWithoutBorders - Android

## Publishing Payload
```python3
pl = b'g'
encrypted_content=b'...'
device_id=b'...'

payload = len(encrypted_content) + pl + encrypted_content + device_id
return base64.encode(payload)

#unpacking in Python
import struct
import base64

payload = base64.decode(incoming_payload)
len_enc_content = struct.unpack("<i", payload[0])
pl = payload[1]
encrypted_content = payload[2:len_enc_content]
device_id = payload[2+len_enc_content:]
```
