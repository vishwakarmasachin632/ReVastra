export default function StatCard({ title, value, hint, tone = 'default' }) {
  return (
    <div className={`stat-card ${tone}`}>
      <p>{title}</p>
      <h3>{value}</h3>
      <span>{hint}</span>
    </div>
  );
}
