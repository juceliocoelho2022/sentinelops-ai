param(
    [Parameter(Mandatory = $true)]
    [string]$Secret
)

if ($Secret.Length -lt 32) {
    throw "Secret must contain at least 32 characters."
}

function ConvertTo-Base64Url([byte[]]$Bytes) {
    [Convert]::ToBase64String($Bytes).TrimEnd('=').Replace('+','-').Replace('/','_')
}

$header = '{"alg":"HS256","typ":"JWT"}'
$now = [DateTimeOffset]::UtcNow.ToUnixTimeSeconds()
$exp = $now + 600
$payload = @{
    sub = "local-investigator"
    scope = "incident:investigate"
    iat = $now
    exp = $exp
} | ConvertTo-Json -Compress

$headerEncoded = ConvertTo-Base64Url ([Text.Encoding]::UTF8.GetBytes($header))
$payloadEncoded = ConvertTo-Base64Url ([Text.Encoding]::UTF8.GetBytes($payload))
$unsignedToken = "$headerEncoded.$payloadEncoded"

$hmac = [System.Security.Cryptography.HMACSHA256]::new([Text.Encoding]::UTF8.GetBytes($Secret))
try {
    $signature = ConvertTo-Base64Url ($hmac.ComputeHash([Text.Encoding]::UTF8.GetBytes($unsignedToken)))
}
finally {
    $hmac.Dispose()
}

"$unsignedToken.$signature"
