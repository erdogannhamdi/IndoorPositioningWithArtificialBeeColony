Plotly.d3.csv('http://192.168.1.56/Ploty/data.csv', function(err, rows){
      function unpack(rows, key) {
          return rows.map(function(row) 
          {return row[key]; });
      }
var trace1 = {
  x: unpack(rows, 'X'),
  y: unpack(rows, 'Y'),
  mode: 'lines+markers',
  name: 'spline',
  text: ['tweak line smoothness&lt;br&gt;with "smoothing" in line object', 'tweak line smoothness&lt;br&gt;with "smoothing" in line object', 'tweak line smoothness&lt;br&gt;with "smoothing" in line object', 'tweak line smoothness&lt;br&gt;with "smoothing" in line object', 'tweak line smoothness&lt;br&gt;with "smoothing" in line object', 'tweak line smoothness&lt;br&gt;with "smoothing" in line object'],
  line: {shape: 'spline'},
  type: 'scatter'
};

var trace2 = {
  x: unpack(rows, 'TX'),
  y: unpack(rows, 'TY'),
  mode: 'lines+markers',
  name: 'spline',
  text: ['tweak line smoothness&lt;br&gt;with "smoothing" in line object', 'tweak line smoothness&lt;br&gt;with "smoothing" in line object', 'tweak line smoothness&lt;br&gt;with "smoothing" in line object', 'tweak line smoothness&lt;br&gt;with "smoothing" in line object', 'tweak line smoothness&lt;br&gt;with "smoothing" in line object', 'tweak line smoothness&lt;br&gt;with "smoothing" in line object'],
  line: {shape: 'spline'},
  type: 'scatter'
  marker: {
    color: 'rgb(164, 194, 244)',
    size: 12,
    line: {
      color: 'white',
      width: 0.5
    }
  },
};

var data = [trace1, trace2];

Plotly.newPlot('myDiv', data, {}, {showSendToCloud: true});
});