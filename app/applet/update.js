const fs = require('fs');
let content = fs.readFileSync('app/src/main/java/com/example/ui/MainScreen.kt', 'utf8');

// Speedometer replacements:
content = content.split('currentTempIndex = (currentTempIndex - 1 + 20) % 20').join('currentTempIndex = (currentTempIndex - 1 + 30) % 30');
content = content.split('currentTempIndex = (currentTempIndex + 1) % 20').join('currentTempIndex = (currentTempIndex + 1) % 30');
content = content.split('DESIGN: ${activeSpeedometerIndex + 1} / 20').join('DESIGN: ${activeSpeedometerIndex + 1} / 30');
content = content.split('DESIGN PREVIEW: ${activeSpeedometerIndex + 1} / 20').join('DESIGN PREVIEW: ${activeSpeedometerIndex + 1} / 30');

// Atmosphere replacements:
content = content.split('currentTempIndex = (currentTempIndex - 1 + 10) % 10').join('currentTempIndex = (currentTempIndex - 1 + 30) % 30');
content = content.split('currentTempIndex = (currentTempIndex + 1) % 10').join('currentTempIndex = (currentTempIndex + 1) % 30');
content = content.split('STYLE PREVIEW: ${activeAtmosphereIndex + 1} / 10').join('STYLE PREVIEW: ${activeAtmosphereIndex + 1} / 30');

fs.writeFileSync('app/src/main/java/com/example/ui/MainScreen.kt', content, 'utf8');
console.log('Update successful!');
